package com.svail.dataTransfer;

import com.mongodb.*;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

/**
 *
 * BasicData_Resold_50:4600018
   Deals_BasicDataMeetDeal：170688
   Deals_aiwujiwu:222211
   Deals_community:266787
   Deals_fang:200803
   Deals_lianjia:30140
   Deals_woaiwojia:475
 *
 *
 */
public class CopyCollections {
    public static void main(String[] args) throws UnsupportedEncodingException {

        Mongo m_export=null;
        Mongo m_import=null;

        try {
            m_import=new Mongo("192.168.6.9",27017);
            m_export=new Mongo("127.0.0.1",27017);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DB db_import=m_import.getDB("paper");
        DB db_export=m_export.getDB("paper");

        DBCollection coll_export=db_export.getCollection("GridData_Resold");
        DBCollection coll_import=db_import.getCollection("GridData_Resold");

        DBCursor cs=coll_export.find();
        BasicDBObject document;

        int count=0;
        System.out.println("begin:");
        while(cs.hasNext()){
            try{
                count++;
                document=(BasicDBObject)cs.next();
                System.out.println(document);

                coll_import.insert(document);
                System.out.println(count);
            }catch( MongoInternalException e){
                e.getStackTrace();
            }
        }
    }
}
