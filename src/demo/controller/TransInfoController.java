package demo.controller;


import com.alibaba.druid.support.json.JSONUtils;
import demo.service.TransInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/10.
 */
@Controller
@RequestMapping(value="/transInfo")
public class TransInfoController {


    @Resource
    private TransInfoService transInfoService;




    @RequestMapping("/transIndex")
    public String trnasInfoList(HttpServletRequest request,HttpServletResponse response,@RequestParam Map<String,String> param){

      return  "transList";
    }

//    @RequestMapping("/transList")
//    public void transList(HttpServletRequest request,HttpServletResponse response,@RequestParam Map<String,String> param){
//
//
//
//
//        List<Map<String,Object>> list = transInfoService.transInfoList(param);
//
//        System.out.print(list.size());
//        String s = JSONUtils.toJSONString(list);
//        PrintWriter pw = null;
//        try {
//            pw = response.getWriter();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        pw.write(s);
//    }

    @RequestMapping("/transList")
    public void transList(HttpServletRequest request,HttpServletResponse response,@RequestParam Map<String,String> param){




        List<Map<String,Object>> list = transInfoService.transInfoList(param);

        System.out.print(list.size());
        String s = JSONUtils.toJSONString(list);
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pw.println(s);
    }

    @RequestMapping("/transCount")
    public void trnasCount(HttpServletRequest request,HttpServletResponse response,@RequestParam Map<String,String> param){

        PrintWriter pw = null;
        try {
            pw = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pw.write("10");
    }

    @RequestMapping("/default")
    public String transDefault(){
        return "/layout/default";
    }

    @RequestMapping("transDetail")
    public void transDetail(HttpServletRequest request,HttpServletResponse response,@RequestParam Map<String,String> param){

        Map<String,Object> map =  transInfoService.transInfoDetail(param);
        String s = JSONUtils.toJSONString(map);
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pw.write(s);

    }

    @RequestMapping("/transDetailPage")
    public String transDetailPage(){
        return "transDetail";
    }


    @RequestMapping("/cardBin")
    public void cardBin(HttpServletRequest request,HttpServletResponse response,@RequestParam Map<String,String> param){

        transInfoService.cardBin();

    }

}
