package com.zblog.backend.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.zblog.biz.PostManager;
import com.zblog.common.dal.entity.Post;
import com.zblog.common.plugin.MapContainer;
import com.zblog.common.plugin.PageModel;
import com.zblog.common.util.JsoupUtils;
import com.zblog.common.util.constants.PostConstants;
import com.zblog.service.CategoryService;
import com.zblog.service.PostService;

@Controller(value = "bPostController")
@RequestMapping("/backend/posts")
public class PostController{
  @Autowired
  private PostService postService;
  @Autowired
  private PostManager postManager;
  @Autowired
  private CategoryService categoryService;

  @RequestMapping(method = RequestMethod.GET)
  public String index(@RequestParam(value = "page", defaultValue = "1") int page, Model model){
    PageModel pageModel = postService.listPost(page, 15);
    model.addAttribute("page", pageModel);
    return "backend/post/list";
  }

  @ResponseBody
  @RequestMapping(method = RequestMethod.POST)
  public Object insert(Post post, String uploadToken){
    post.setId(postService.createPostid());
    post.setLastUpdate(new Date());
    
    /* 由于加入xss的过滤,html内容都被转义了,这里需呀unescape */
    String content=HtmlUtils.htmlUnescape(post.getContent());
    post.setContent(JsoupUtils.filter(content));
    String cleanTxt = JsoupUtils.plainText(content);
    post.setExcerpt(cleanTxt.length() > PostConstants.EXCERPT_LENGTH ? cleanTxt.substring(0,
        PostConstants.EXCERPT_LENGTH) : cleanTxt);
    
    post.setCreator("admin");
    postManager.insertPost(post, uploadToken);
    return new MapContainer("success", true);
  }

  @ResponseBody
  @RequestMapping(value = "/{postid}", method = RequestMethod.DELETE)
  public Object remove(@PathVariable("postid") String postid){
    postManager.removePost(postid);
    return new MapContainer("success", true);
  }

  @RequestMapping(value = "/edit", method = RequestMethod.GET)
  public String edit(Model model){
    model.addAttribute("categorys", categoryService.list());
    return "backend/post/edit";
  }

}
