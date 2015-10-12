package demo.service;

import com.sun.javafx.binding.StringFormatter;
import demo.util.Dao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/10.
 */
@Service
public class TransInfoService {


    @Resource
    private Dao dao;

    public List<Map<String,Object>> transInfoList(Map<String,String> params){


        String sb = " 30";
        if(params.get("page") != null) {
            int page = Integer.parseInt(params.get("page"));
          sb =  (page - 1) * 30 + " ,"+30;
        }
        String sql= "select * from trans_info where 1=1 ";
        if(params.get("referenceNo") != null&&!params.get("referenceNo").isEmpty()){
            sql+=(" and acq_reference_no = " + params.get("referenceNo"));
        }
        if(params.get("merchantno") != null&&!params.get("merchantno").isEmpty()){
            sql+=(" and merchant_no = " + params.get("merchantno"));
        }
        if(params.get("transDateBegin") != null&&params.get("transDateEnd") != null&&!params.get("transDateBegin").isEmpty() && !params.get("transDateEnd").isEmpty()){
            sql+=(" and trans_time between  '" + params.get("transDateBegin") + "' and '"+ params.get("transDateEnd") + "'");
        }

        sql += (" order by id desc limit "+sb);

        return  dao.find(sql);
    }

    public Map<String,Object> transInfoDetail(Map<String,String> params){

        String sql = "select * from trans_info where id =  ?";
        String id = params.get("id");
        return dao.findFirst(sql, id);
    }

    public void cardBin(){
        File f = new File("C:\\Users\\Administrator\\Desktop\\临时文件\\跨行清算行号\\跨行清算行号.txt");
        File f1 = new File("C:\\Users\\Administrator\\Desktop\\临时文件\\跨行清算行号\\sql.sql");
       // eptokCarBin();
        //eptokCarBin2();
        try {
            InputStream is = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            OutputStream os = new FileOutputStream(f1);
            String line = br.readLine();
            line = br.readLine();
            StringBuilder sb1 = new StringBuilder();
            StringBuffer sb2 = new StringBuffer();
            StringBuffer sb3 = new StringBuffer();
            while(line != null){
                String[] pa = line.split(",");

                String sql = "select * from pos_card_bin where bank_name like '%"+pa[1].substring(1,pa[1].length() - 2)+"%'";
               Map<String, Object> map = dao.findFirst(sql);
                if(map == null){

                    String sql1 = "select * from pos_card_bin where bank_no = '"+pa[0].substring(1,pa[0].length() -1)+"'";
                    Map<String,Object> map1 = dao.findFirst(sql1);
                    if(map1 ==null){
                        sb1.append(String.format("insert into pos_card_bin (bank_name,create_time,bank_no) values ('"+pa[0].substring(1, pa[0].length() - 1)+"',now(),'"+ pa[1].substring(1, pa[1].length() - 1)+"');"));

                        sb1.append("\r\n");
                    }else{
                        sb3.append(map1.get("bank_name").toString()+"============"+ pa[1].substring(1, pa[1].length()));

                        sb3.append("\r\n");

                    }

                }else if(map.get("bank_no") == null){
                    sb2.append(String.format("update  pos_card_bin set bank_no='{0}' where id = {1};", pa[0].substring(1, pa[0].length() - 1), map.get("id")));
                    sb2.append("\r\n");
                }
                line = br.readLine();
            }

            OutputStreamWriter osw = new OutputStreamWriter(os);

            osw.write(sb2.toString());
            osw.write(sb1.toString());
            osw.write(sb3.toString());
            osw.close();
            os.close();
            isr.close();
            is.close();



        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void eptokCarBin(){

        File f = new File("C:\\Users\\Administrator\\Desktop\\临时文件\\跨行清算行号\\银盛银行.txt");
        File f1 = new File("C:\\Users\\Administrator\\Desktop\\临时文件\\跨行清算行号\\sql2.sql");

        try {
            InputStream is = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(is,"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            OutputStream os = new FileOutputStream(f1);
            String line = br.readLine();
            line = br.readLine();
            StringBuilder sb1 = new StringBuilder();
            StringBuffer sb2 = new StringBuffer();
            StringBuffer sb3 = new StringBuffer();
            int c = 0;
            while(line != null){
                String ids = "";
                System.out.println(line);
                String[] pa = line.split("\\)");

                String bank_name = pa[1];
                String bank_code = pa[0].substring(1);
                String sql = "select * from pos_card_bin where bank_name like '%"+pa[1]+"%'";
                List<Map<String, Object>> map = dao.find(sql);
                if(sb3.indexOf(pa[1]) != -1){
                    line = br.readLine();
                    continue;
                }
                sb3.append(line);
                if(map==null||map.size()<=0){


                    String pas = pa[1].substring(0, 2);

                    String sqls = "select * from pos_card_bin where bank_name like '%浦东发展银行%'";
                    List<Map<String, Object>> maps = dao.find(sqls);
                    if(maps==null||maps.size() <=0){
                        sb1.append("============"+ pa[1]);

                        sb1.append("\r\n");
                    }else{
                        c++;

                        for (Map<String,Object> ma : maps){
                            sb2.append("insert into eptok_card_bin (bank_name,bank_code,card_bin_id) values('"+bank_name+"','"+bank_code+"','"+ma.get("id")+
                           "');");
                            sb2.append("\r\n");
                        }



                    }

                }else {
                    c++;
                    for (Map<String,Object> ma : map){
                        sb2.append("insert into eptok_card_bin (bank_name,bank_code,card_bin_id) values('"+bank_name+"','"+bank_code+"','"+ma.get("id")+
                                "');");
                        sb2.append("\r\n");
                    }
                }
                line = br.readLine();
            }

            OutputStreamWriter osw = new OutputStreamWriter(os);

            osw.write(sb2.toString());
            osw.write(sb1.toString());
            osw.close();
            os.close();
            isr.close();
            is.close();



        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void eptokCarBin2(){

        File f = new File("C:\\Users\\Administrator\\Desktop\\临时文件\\跨行清算行号\\银盛银行.txt");
        File f1 = new File("C:\\Users\\Administrator\\Desktop\\临时文件\\跨行清算行号\\sql2.sql");

        try {
            InputStream is = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            OutputStream os = new FileOutputStream(f1);
            String line = br.readLine();
            line = br.readLine();
            StringBuilder sb1 = new StringBuilder();
            StringBuffer sb2 = new StringBuffer();
            StringBuffer sb3 = new StringBuffer();


            String sqls = "select * from pos_card_bin where bank_name like '%邮储%'";
            List<Map<String, Object>> maps = dao.find(sqls);
                for (Map<String, Object> ma : maps) {
                    sb2.append("insert into eptok_card_bin (bank_name,bank_code,card_bin_id) values('邮政银行','4031000','" + ma.get("id") +
                            "');");
                    sb2.append("\r\n");
                }





            OutputStreamWriter osw = new OutputStreamWriter(os);

            osw.write(sb2.toString());
            osw.write(sb1.toString());
            osw.close();
            os.close();
            isr.close();
            is.close();

        }catch (Exception e){

        }


    }

}
