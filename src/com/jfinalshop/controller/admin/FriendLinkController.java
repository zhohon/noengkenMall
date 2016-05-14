package com.jfinalshop.controller.admin;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinalshop.model.FriendLink;
import com.jfinalshop.util.CommonUtil;
import com.jfinalshop.validator.admin.FriendLinkValidator;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * 后台类 - 友情链接
 * 
 */
public class FriendLinkController extends BaseAdminController<FriendLink>{
	
	private FriendLink friendLink;
	private File logo;
	private String logoFileName;
	
	// 添加
	public void add() {
		render("/admin/friend_link_input.html");
	}
	
	// 编辑
	public void edit() {
		String id = getPara("id","");
		if(StrKit.notBlank(id)){
			setAttr("friendLink", FriendLink.dao.findById(id));
		}		
		render("/admin/friend_link_input.html");
	}

	// 列表
	public void list() {
		findByPage();
		render("/admin/friend_link_list.html");
	}
		
	
	// 保存
	@Before(FriendLinkValidator.class)
	public void save() throws IOException{
		friendLink = getModel(FriendLink.class);
        String imgUrl = handleUpload(FileType.IMAGE);
        if (imgUrl != null) {
            friendLink.set("img", imgUrl);
        }
		friendLink.set("id", CommonUtil.getUUID());
		friendLink.set("createDate", new Date());
		friendLink.save();
		redirect("/friendLink/list");
	}
	
	// 更新
	@Before(FriendLinkValidator.class)
	public void update() throws IOException{
		friendLink = getModel(FriendLink.class);
        String imgUrl = handleUpload(FileType.IMAGE);
        if (imgUrl != null) {
            friendLink.set("img", imgUrl);
        }
		friendLink.set("modifyDate", new Date());
		friendLink.update();
		redirect("/friendLink/list");
	}
	
	// 删除
	public void delete(){
		String[] ids = getParaValues("ids");
		for (String id : ids) {
			if(FriendLink.dao.delete(id)){	
				ajaxJsonSuccessMessage("删除成功！");
			}else{
				ajaxJsonErrorMessage("删除失败！");
			}
		}	
	}
	
}
