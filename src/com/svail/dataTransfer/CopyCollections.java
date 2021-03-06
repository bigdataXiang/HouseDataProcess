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

            m_export=new Mongo("127.0.0.1",27017);
            m_import=new Mongo("192.168.6.9",27017);
        } catch (Exception e) {
            e.printStackTrace();
        }


        DB db_export=m_export.getDB("InvestmentEvolution");
        DBCollection coll_export=db_export.getCollection("BasicData_Resold_gd_plus");

        DB db_import=m_import.getDB("InvestmentEvolution");
        DBCollection coll_import=db_import.getCollection("BasicData_Resold_gd_plus");


        DBCursor cs=coll_export.find();
        BasicDBObject document;

        int count=0;
        System.out.println("begin:");
        while(cs.hasNext()){
            try{
                count++;
                document=(BasicDBObject)cs.next();
                //System.out.println(document);

                coll_import.insert(document);
            }catch( MongoInternalException e){
                e.getStackTrace();
            }
        }
        System.out.println(count);
    }
}



