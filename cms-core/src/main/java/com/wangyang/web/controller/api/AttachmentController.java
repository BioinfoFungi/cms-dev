package com.wangyang.web.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.wangyang.common.BaseResponse;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.entity.Literature;
import com.wangyang.service.IAttachmentService;
import com.wangyang.pojo.entity.Attachment;
import com.wangyang.pojo.params.AttachmentParam;
import com.wangyang.service.ILiteratureService;
import org.hibernate.id.uuid.StandardRandomStrategy;
import org.jbibtex.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/attachment")
public class AttachmentController {

    @Autowired
    IAttachmentService attachmentService;

    @Autowired
    ILiteratureService literatureService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @RequestPart("file")
    public Attachment upload(@RequestPart("file") MultipartFile file){
        return  attachmentService.upload(file);
    }
    @PostMapping(value = "/uploadBib", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @RequestPart("file")
    public BaseResponse uploadBib(@RequestPart("file") MultipartFile file,@RequestParam(value = "update", defaultValue = "false") Boolean update){
        try {
            BibTeXParser bibTeXParser =  new BibTeXParser();
            BibTeXDatabase bibTeXDatabase = bibTeXParser.parse(new InputStreamReader(file.getInputStream()));
            Map<Key, BibTeXEntry> entries = bibTeXDatabase.getEntries();
            List<Literature> literatureList = literatureService.listAll();
            Map<String, Literature> literatureMap = ServiceUtil.convertToMap(literatureList, Literature::getKey);
            List<Literature> newLiteratureList = new ArrayList<>();
            for(BibTeXEntry bibTeXEntry :entries.values()){
                Key entryKey = bibTeXEntry.getKey();
                Literature literature  = new Literature();
                if(literatureMap.containsKey(entryKey.getValue())){
                    if(!update){
                        continue;
                    }else {
                        literature=literatureMap.get(entryKey.getValue());
                    }

                }
                String title = ((StringValue) bibTeXEntry.getField(BibTeXEntry.KEY_TITLE)).getString();
                if(title.indexOf("{")>-1){
                        title = title.replace("{","");
                        title = title.replace("}","");
                }


//                ((StringValue) bibTeXEntry.getField(BibTeXEntry.KEY_PAGES)).getString();
                literature.setKey(bibTeXEntry.getKey().getValue());
                Value pmcidValue = bibTeXEntry.getField(new Key("pmcid"));
                if(pmcidValue!=null){
                    String pmcid = ((StringValue) pmcidValue).getString();
                    literature.setUrl("https://www.ncbi.nlm.nih.gov/pmc/articles/"+pmcid);
                } else {
                    literature.setUrl("");
                }

                literature.setTitle(title);
                newLiteratureList.add(literature);

            }
            literatureService.saveAll(newLiteratureList);
            return  BaseResponse.ok("success: "+newLiteratureList.size());
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            return  BaseResponse.error("error!!");
        }


    }


//    {
//        success : 0 | 1,           // 0 表示上传失败，1 表示上传成功
//                message : "提示的信息，上传成功或上传失败及错误信息等。",
//            url     : "图片地址"        // 上传成功时才返回
//    }
    @PostMapping(value = "/editormd", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String editormd(@RequestPart("editormd-image-file") MultipartFile file){
        Attachment attachment = attachmentService.upload(file);
        JSONObject object = new JSONObject();
        object.put("success",1);
//        object.put("message","success");
        object.put("url",attachment.getPath());
        return object.toJSONString();
    }
//    @PostMapping(value = "/testUpload")
//    public Attachment testUpload(String name, @RequestPart(required = false)MultipartFile file2){
//        if(file2!=null){
//            return  attachmentService.upload(file2);
//        }
//        return new Attachment();
//    }
//    @PostMapping(value = "/testUpload2")
////    @RequestPart("file")
//    public Attachment testUpload2( String name,MultipartFile file2){
//        return new Attachment();
//    }


    @RequestMapping(value = "/find/{id}")
    public Attachment findById(@PathVariable("id") Integer id){
        return  attachmentService.findById(id);
    }

    /**
     * 上传文字内容到文件 SVG
     * @param attachmentParam
     * @return
     */
    @RequestMapping(value = "/uploadStrContent")
    public Attachment uploadStrContent(@RequestBody AttachmentParam attachmentParam){
        return  attachmentService.uploadStrContent(attachmentParam);
    }

    /**
     * 更新文字内容
     * @param attachmentId
     * @param attachmentParam
     * @return
     */
    @RequestMapping(value = "/uploadStrContent/{attachmentId}")
    public Attachment updateStrContent(@PathVariable("attachmentId")Integer attachmentId,@RequestBody  AttachmentParam attachmentParam){
        return  attachmentService.uploadStrContent(attachmentId,attachmentParam);
    }


    @GetMapping
    public Page<Attachment> list(@PageableDefault (sort = {"id"},direction = DESC)Pageable pageable){
        return attachmentService.list(pageable);
    }

    @RequestMapping("/delete/{id}")
    public  Attachment deleteById(@PathVariable("id") Integer id){
        return attachmentService.deleteById(id);
    }

    @RequestMapping("/delete")
    public List<Attachment> deleteByIds(Collection ids){
        return attachmentService.deleteByIds(ids);
    }



}
