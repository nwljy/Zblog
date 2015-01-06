package com.zblog.web.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zblog.biz.aop.PostIndexManager;
import com.zblog.common.dal.entity.Option;
import com.zblog.common.plugin.MapContainer;
import com.zblog.common.util.StringUtils;
import com.zblog.common.util.constants.WebConstants;
import com.zblog.service.PostService;
import com.zblog.template.FreeMarkerUtils;

@Controller
public class IndexController{
  @Autowired
  private PostService postService;
  @Autowired
  private PostIndexManager postIndexManager;

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String index(@RequestParam(value = "page", defaultValue = "1") int page, String word, Model model){
    if(!StringUtils.isBlank(word)){
      model.addAttribute("page", postIndexManager.search(word, page));
    }else{
      model.addAttribute("page", postService.listPost(page, 10));
    }
    return "index";
  }

  @RequestMapping("/init.json")
  public void init(@ModelAttribute Option option){
    MapContainer map = new MapContainer("title", WebConstants.TITLE);
    map.put("description", WebConstants.DESCRIPTION);
    map.put("domain", WebConstants.DOMAIN);
    map.put("backdomain", WebConstants.DOMAIN + "/backend");
    FreeMarkerUtils.genHtml("/common/head.html", new File(WebConstants.APPLICATION_PATH, WebConstants.PREFIX
        + "/common/head.html"), map);

    FreeMarkerUtils.genHtml("/common/footer.html", new File(WebConstants.APPLICATION_PATH, WebConstants.PREFIX
        + "/common/footer.html"), map);
    FreeMarkerUtils.genHtml("/common/comments_form.html", new File(WebConstants.APPLICATION_PATH, WebConstants.PREFIX
        + "/common/comments_form.html"), map);
    FreeMarkerUtils.genHtml("/common/bootstrap.html", new File(WebConstants.APPLICATION_PATH, WebConstants.PREFIX
        + "/common/bootstrap.html"), map);
  }

}
