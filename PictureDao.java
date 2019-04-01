package com.dao;

import com.daomain.PagePicture;
import com.daomain.Picture;
import com.daomain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
@Repository
public class PictureDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }


    //检索相关图片
    public Map findPictureByName(String picName){
        String sql="select pic_id from picture where name like %?%";
        Map a=jdbcTemplate.queryForMap(sql,new Object[]{picName});
        return a;
    }

    public int CountPictureNum(String limit){
        String sql=String.format("select Count(*) from picture %s",limit);
        Map a=jdbcTemplate.queryForMap(sql);
        int page=Integer.parseInt(a.get("Count(*)").toString());
        return page;
    }


    //统计页面数量
    public int CountPictureByPage(PagePicture pagePicture) {
        String sql = "select Count(*) from picture";
        Map a=jdbcTemplate.queryForMap(sql);
        int page=Integer.parseInt(a.get("Count(*)").toString());
        pagePicture.setPageCount(page);
        int pageSize=12;
        int pages = 0;
        if (page % pageSize == 0)
            pages = page / pageSize;
        else
            pages = page / pageSize + 1;
        return pages;
    }

    //返回每页内容
    public List sortPictureByPage(PagePicture pagePicture){
        String sql = "select * from picture limit ?,12";
        if(pagePicture.getCurrentPage()==0){
            pagePicture.setCurrentPage(1);
        }
        List result=jdbcTemplate.queryForList(sql,new Object[]{(pagePicture.getCurrentPage()-1)*12});
        return result;
    }

    //插入图片数据
    public void InsertPicture(Picture picture){
        String sql="insert into picture(pic_name,author_id,discription,pic_time)" +
                "values (?,?,?,?)";

        jdbcTemplate.update(sql,new Object[]{picture.getPicName(),
                picture.getAuthorId(),picture.getDiscription(),picture.getPicTime()});
    }

    //更新图片收藏量
    public void colCountAdd(Picture picture){
        String sql="update picture set likenum=likenum+1 where pic_id=? ";
        jdbcTemplate.update(sql,new Object[]{picture.getPicId()});
    }

    //放大后图片翻页
    public Picture lastPicture(int pic_id){
        final Picture picture=new Picture();
        String sql="select * from picture where pic_id=?";

        jdbcTemplate.query(sql,new Object[]{pic_id},new RowCallbackHandler() {
            public void processRow(ResultSet resultSet) throws SQLException {
                picture.setPicId(resultSet.getInt("Pic_id"));
                picture.setPicName(resultSet.getString("pic_name"));
                picture.setLikeNum(resultSet.getInt("likeNum"));
                picture.setDiscription(resultSet.getString("discription"));
            }
        });
        return picture;
    }

    //点赞+1或取消赞
    public void likePicture(int picId,User user,String action){
       if(action.equals("p")){
            String sql_insert="Insert into collect (pic_id,user_id) values (?,?)";
            String sql_up2="update picture set likeNum=likeNum+1 where pic_id=?";
            jdbcTemplate.update(sql_up2,new Object[]{picId});
            jdbcTemplate.update(sql_insert,new Object[]{picId,user.getUserId()});
       }
        else {
           String sql_up1="update picture set likeNum=likeNum-1 where pic_id=?";
           String sql_del="delete from collect where pic_id=? and user_id=?";
           jdbcTemplate.update(sql_up1,new Object[]{picId});
           jdbcTemplate.update(sql_del,new Object[]{picId,user.getUserId()});
       }
    }

    //检查是否点赞
    public boolean checkLfLike(int picId,User user){
        String sql="select col_id from collect where pic_id=? and user_id=?";
        List list=jdbcTemplate.queryForList(sql,new Object[]{picId,user.getUserId()});
        System.out.println(list);

        if(list.size()>0){
            return true;
        }else {
        return false;
        }
    }

    public int CountPicturePage(PagePicture pagePicture,String action) {
        String sql=String.format("select Count(*) from picture %s",action);
        Map a=jdbcTemplate.queryForMap(sql);
        int page=Integer.parseInt(a.get("Count(*)").toString());
        pagePicture.setPageCount(page);
        int pageSize=12;
        int pages = 0;
        if (page % pageSize == 0)
            pages = page / pageSize;
        else
            pages = page / pageSize + 1;
        return pages;
    }

    //返回每页内容
    public List sortPicturePage(PagePicture pagePicture,String action){
        String sql =String.format("select * from picture %s limit ?,12",action);
        if(pagePicture.getCurrentPage()==0){
            pagePicture.setCurrentPage(1);
        }
        List result=jdbcTemplate.queryForList(sql,new Object[]{(pagePicture.getCurrentPage()-1)*12});
        return result;
    }





}
