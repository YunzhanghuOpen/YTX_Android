package com.yuntongxun.ecdemo.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yuntongxun.ecdemo.ECApplication;
import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.common.utils.LogUtil;
import com.yuntongxun.ecdemo.core.comparator.ServerConfigBean;
import com.yuntongxun.ecsdk.ECMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luhuashan on 16/3/17.
 */
public class ServerConfigSqlManager  {

    static final String SERVER_CONFIG_DATABASE_NAME = "ECDEMO_ServerConfig.db";
    static final String SERVER_CONFIG_TABLE_NAME = "serverconfig";

    private static SQLiteDatabase sqliteDB;
    private static ServerConfigSqlManager instance;

    static class ServerConfigColumn  {

        public static final String SERVER_ITEM_NAME = "server_item_name";
        public static final String ITEM_ID = "server_item_id";
        public static final String CONNECT_IP = "connect_ip";
        public static final String CONNECT_PORT = "connect_port";
        public static final String LVS_IP = "lvs_ip";
        public static final String LVS_PORT = "lvs_port";
        public static final String FILESERVER_IP = "fileserver_ip";
        public static final String FILESERVER_PORT = "fileserver_port";
        public static final String APPID = "appid";
        public static final String APPTOKEN = "apptoken";
    }

    public static ServerConfigSqlManager getInstance(){
        if(instance==null){
            return  new ServerConfigSqlManager();
        }
        return  instance;
    }
    private static ServerConfigDB sqliteDBHelper;

    private ServerConfigSqlManager(){
         sqliteDBHelper=new ServerConfigDB(ECApplication.getInstance(),null,null, CCPAppManager.getVersionCode());
        if(sqliteDB==null){
            sqliteDB=sqliteDBHelper.getWritableDatabase();
        }

    }

    public static void destory(){
        if(sqliteDBHelper!=null){
            sqliteDBHelper.close();
            sqliteDBHelper=null;
        }
        if(sqliteDB!=null){
            sqliteDB.close();
            sqliteDB=null;
        }
        if(instance!=null){
            instance=null;
        }


    }




    static class ServerConfigDB extends SQLiteOpenHelper{


        public ServerConfigDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, SERVER_CONFIG_DATABASE_NAME, factory, version);
        }



        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

         createServerConfigTable(sqLiteDatabase);

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }

        void createServerConfigTable(SQLiteDatabase db) {

            String sql = "CREATE TABLE IF NOT EXISTS "
                    + SERVER_CONFIG_TABLE_NAME
                    + " ("
                    + ServerConfigColumn.ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ServerConfigColumn.SERVER_ITEM_NAME + " TEXT, "
                    + ServerConfigColumn.CONNECT_IP + " TEXT, "
                    + ServerConfigColumn.CONNECT_PORT + " TEXT, "
                    + ServerConfigColumn.LVS_IP + " TEXT, "
                    + ServerConfigColumn.LVS_PORT + " TEXT, "
                    + ServerConfigColumn.FILESERVER_IP + " TEXT, "
                    + ServerConfigColumn.FILESERVER_PORT + " TEXT, "
                    + ServerConfigColumn.APPID + " TEXT, "
                    + ServerConfigColumn.APPTOKEN + " TEXT "
                    + ")";
            db.execSQL(sql);
        }

    }


   public static long insertServerConfig(ServerConfigBean serverConfigBean){
            if(serverConfigBean==null){
                return 0;
            }
       ContentValues values = new ContentValues();
       values.put(ServerConfigColumn.SERVER_ITEM_NAME,serverConfigBean.getName());
       values.put(ServerConfigColumn.CONNECT_IP,serverConfigBean.getConnectip());
       values.put(ServerConfigColumn.CONNECT_PORT,serverConfigBean.getConnectport());
       values.put(ServerConfigColumn.LVS_IP,serverConfigBean.getLvsip());
       values.put(ServerConfigColumn.LVS_PORT,serverConfigBean.getLvsport());
       values.put(ServerConfigColumn.FILESERVER_IP,serverConfigBean.getFileip());
       values.put(ServerConfigColumn.FILESERVER_PORT,serverConfigBean.getFileport());
       values.put(ServerConfigColumn.APPID,serverConfigBean.getAppid());
       values.put(ServerConfigColumn.APPTOKEN,serverConfigBean.getApptoken());
       return sqliteDB.insert(SERVER_CONFIG_TABLE_NAME, null, values);
   }
   public static long updateServerConfig(ServerConfigBean serverConfigBean,String name){
            if(serverConfigBean==null){
                return 0;
            }
       String where = ServerConfigColumn.SERVER_ITEM_NAME + " = '" + name + "' ";
       ContentValues values = new ContentValues();
       values.put(ServerConfigColumn.SERVER_ITEM_NAME,serverConfigBean.getName());
       values.put(ServerConfigColumn.CONNECT_IP,serverConfigBean.getConnectip());
       values.put(ServerConfigColumn.CONNECT_PORT,serverConfigBean.getConnectport());
       values.put(ServerConfigColumn.LVS_IP,serverConfigBean.getLvsip());
       values.put(ServerConfigColumn.LVS_PORT,serverConfigBean.getLvsport());
       values.put(ServerConfigColumn.FILESERVER_IP,serverConfigBean.getFileip());
       values.put(ServerConfigColumn.FILESERVER_PORT,serverConfigBean.getFileport());
       values.put(ServerConfigColumn.APPID,serverConfigBean.getAppid());
       values.put(ServerConfigColumn.APPTOKEN,serverConfigBean.getApptoken());
       return sqliteDB.update(SERVER_CONFIG_TABLE_NAME, values, where,null);
   }

   public static boolean isConfigItemExist(String name){

       String sql = "select server_item_id from "+ SERVER_CONFIG_TABLE_NAME+" where server_item_name ='" + name + "'";
       try {
           Cursor cursor = sqliteDB.rawQuery(sql, null);
           if(cursor != null && cursor.getCount() > 0) {
               return true;
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
       return false;
   }






    public static void deleteServerConfig(String serverItemId){
        sqliteDB.delete(SERVER_CONFIG_TABLE_NAME,
                "server_item_id='" + serverItemId + "'", null);
    }

    public static List<ServerConfigBean> queryServerConfigs(){
        List<ServerConfigBean> list=new ArrayList<ServerConfigBean>();
        Cursor cursor = null;
            try {
                cursor = sqliteDB.query(
                        SERVER_CONFIG_TABLE_NAME, null, null,
                        null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    int count= cursor.getCount();
                    for(int i=0;i<count;i++) {
                        if (cursor.moveToNext()) {
                            ServerConfigBean bean = new ServerConfigBean();
                            bean.setName(cursor.getString(cursor.getColumnIndexOrThrow(ServerConfigColumn.SERVER_ITEM_NAME)));
                            bean.setId(cursor.getString(cursor.getColumnIndexOrThrow(ServerConfigColumn.ITEM_ID)));
                            bean.setConnectip(cursor.getString(cursor.getColumnIndexOrThrow(ServerConfigColumn.CONNECT_IP)));
                            bean.setConnectport(cursor.getString(cursor.getColumnIndexOrThrow(ServerConfigColumn.CONNECT_PORT)));
                            bean.setLvsip(cursor.getString(cursor.getColumnIndexOrThrow(ServerConfigColumn.LVS_IP)));
                            bean.setLvsport(cursor.getString(cursor.getColumnIndexOrThrow(ServerConfigColumn.LVS_PORT)));
                            bean.setFileip(cursor.getString(cursor.getColumnIndexOrThrow(ServerConfigColumn.FILESERVER_IP)));
                            bean.setFileport(cursor.getString(cursor.getColumnIndexOrThrow(ServerConfigColumn.FILESERVER_PORT)));
                            bean.setAppid(cursor.getString(cursor.getColumnIndexOrThrow(ServerConfigColumn.APPID)));
                            bean.setApptoken(cursor.getString(cursor.getColumnIndexOrThrow(ServerConfigColumn.APPTOKEN)));
                            list.add(bean);
                        }
                    }

                }
            } catch (SQLException e) {
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }


      return  list;

  }


}
