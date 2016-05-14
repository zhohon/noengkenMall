package com.jfinalshop.controller.admin;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.cache.ClearCache;
import com.jfinal.plugin.cache.J2CacheName;
import com.jfinalshop.bean.ProductImage;
import com.jfinalshop.model.*;
import com.jfinalshop.service.ProductImageService;
import com.jfinalshop.util.CommonUtil;
import com.jfinalshop.util.FieldMapUtil;
import com.jfinalshop.validator.admin.PacketValidator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by dextrys on 2016/1/8.
 */
public class PacketController extends BaseAdminController<Packet> {
    private Packet packet = new Packet();

    public static final String PACKET_TYPE_SINGLE = "1";

    public static final String PACKET_TYPE_MULTI = "2";

    public void list() {
        setAttr("shelfList", getShelf());
        findByPageAndTime();
        render("/admin/packet_list.html");
    }

    public void add() {
        setAttr("shelfList", getShelf());
        setAttr("productCategoryTreeList", getProductCategoryTreeList());
        render("/admin/packet_input.html");
    }

    public void productPackage() {
        findByPageAndTime();
        setAttr("shelfList", getShelf());
        setAttr("productCategoryTreeList", getProductCategoryTreeList());
        render("/admin/product_package_input.html");
    }

    @Before({PacketValidator.class, ClearCache.class, Tx.class})
    @J2CacheName(Packet.class)
    public void addSinglePackage() {
        String packetType = getPara("packagetype");
        String[] selectProducts = getParaValues("products");
        packet = getModel(Packet.class);
        String shelfid = packet.get("shelf_id");
        List<Packet> packets = new ArrayList<Packet>();
        List<PacketProductMap> packetProductMaps = new ArrayList<PacketProductMap>();
        for (String productid : selectProducts) {
            Product product = Product.dao.findById(productid);
            Packet newPacket = new Packet();
            newPacket._setAttrs(packet);
            newPacket.set("name", product.get("name"));
            BigDecimal newprice = new BigDecimal(0);
            if (packet.getInt("disPercent") > 0) {
                BigDecimal price = product.getBigDecimal("price");
                newprice = price.multiply(new BigDecimal(packet.getInt("disPercent")).divide(new BigDecimal(100)));
            }
            if (packet.getInt("disAmount") > 0) {
                newprice = newprice.subtract(new BigDecimal(packet.getInt("disAmount")));
            }
            newPacket.set("imageText", product.get("description"));
            newPacket.set("img", product.getStr("productImageListStore"));

            newPacket.set("productCategory_id", product.get("productCategory_id"));
            newPacket.set("price", newprice);
            newPacket.set("productType_id", product.get("productType_id"));
            newPacket.set("id",CommonUtil.getUUID());
            packets.add(newPacket);
            PacketProductMap packetProductMap = createPacketPackageMap(product, newPacket);
            packetProductMaps.add(packetProductMap);
        }
        Db.batchSave(packets, 500);
        Db.batchSave(packetProductMaps, 500);
        if (!StrKit.isBlank(shelfid))
            Shelf.dao.updatePacketNum(selectProducts.length, shelfid);
        findProductByPage();
        setAttr("shelfList", getShelf());
        setAttr("productCategoryTreeList", getProductCategoryTreeList());
        setAttr("success", "提交成功");
        render("/admin/product_package_input.html");
    }

    @Before({PacketValidator.class, ClearCache.class, Tx.class})
    @J2CacheName(Packet.class)
    public void addMultiPackage() {
        String[] selectProducts = getParaValues("products");
        packet = getModel(Packet.class);
        String shelfId = packet.get("shelf_id");
        packet.set("isMultiple", 1);
        packet.set("id", CommonUtil.getUUID());
        BigDecimal totalPrice = new BigDecimal(0);
        List<ProductImage> productImageList = new ArrayList<ProductImage>();
        uploadFile = getFile();
        ProductImage productImage = ProductImageService.service.buildProductImage(uploadFile.getFile());
        productImageList.add(productImage);
        List<PacketProductMap> packetProductMaps = new ArrayList<PacketProductMap>();
        for (String productid : selectProducts) {
            Product product = Product.dao.findById(productid);
            totalPrice = totalPrice.add(product.getBigDecimal("price"));
            PacketProductMap packetProductMap = createPacketPackageMap(product, packet);
            packetProductMaps.add(packetProductMap);
            List<ProductImage> list = JSON.parseArray(product.getStr("productImageListStore"), ProductImage.class);
            productImageList.addAll(list);
        }
        Db.batchSave(packetProductMaps, 500);
        if (packet.getInt("disPercent") > 0) {
            totalPrice = totalPrice.multiply(new BigDecimal(packet.getInt("disPercent")).divide(new BigDecimal(100)));
        }
        if (packet.getInt("disAmount") > 0) {
            totalPrice = totalPrice.subtract(new BigDecimal(packet.getInt("disAmount")));
        }
        String jsonText = JSON.toJSONString(productImageList, true);
        packet.set("img", jsonText);
        packet.set("price", totalPrice);
        packet.save();
        if (!StrKit.isBlank(shelfId))
            Shelf.dao.updatePacketNum(1, shelfId);
        findProductByPage();
        setAttr("shelfList", getShelf());
        setAttr("productCategoryTreeList", getProductCategoryTreeList());
        setAttr("success", "提交成功");
        render("/admin/product_package_input.html");
    }

    private PacketProductMap createPacketPackageMap(Product product, Packet packet) {
        PacketProductMap packetProductMap = new PacketProductMap();
        packetProductMap.set("productName", product.get("name"));
        packetProductMap.set("productDesc", product.get("MetaDescription"));
        packetProductMap.set("price", product.get("price"));
        packetProductMap.set("promotionPrice", product.get("marketPrice"));
        packetProductMap.set("num", 1);
        packetProductMap.set("packet_id", packet.get("id"));
        packetProductMap.set("product_id", product.get("id"));
        packetProductMap.set("id",CommonUtil.getUUID());
        return packetProductMap;
    }

    @Before({PacketValidator.class, ClearCache.class})
    @J2CacheName(Packet.class)
    public void save() {
        Packet packet = getModel(Packet.class);
        packet.set("img", handleUpload(FileType.IMAGE));
        saved(packet);
        redirect("/packet/list");
    }

    public void edit() {
        String id = getPara("id", "");
        if (StrKit.notBlank(id)) {
            Packet packet = Packet.dao.findById(id);
            setAttr("packet", packet);
        }
        setAttr("shelfList", getShelf());
        setAttr("productCategoryTreeList", getProductCategoryTreeList());
        render("/admin/packet_input.html");
    }

    @Before({PacketValidator.class, ClearCache.class})
    @J2CacheName(Packet.class)
    public void update() {
        Packet packet = getModel(Packet.class);
        List<Product> products = Packet.dao.getPacketMapProduct(packet.getStr("id"));
        BigDecimal newprice = new BigDecimal(0);

        uploadFile = getFile();
        List<ProductImage> productImageList = new ArrayList<ProductImage>();
        if (uploadFile != null) {
            ProductImage productImage = ProductImageService.service.buildProductImage(uploadFile.getFile());
            productImageList.add(productImage);
        }
        for (Product product : products) {
            newprice = newprice.add(product.getBigDecimal("price"));
            if (uploadFile != null) {
                List<ProductImage> list = JSON.parseArray(product.getStr("productImageListStore"), ProductImage.class);
                productImageList.addAll(list);
            }
        }
        if (packet.getInt("disPercent") > 0) {
            newprice = newprice.multiply(new BigDecimal(packet.getInt("disPercent")).divide(new BigDecimal(100)));
        }
        if (packet.getInt("disAmount") > 0) {
            newprice = newprice.subtract(new BigDecimal(packet.getInt("disAmount")));
        }
        if (uploadFile != null) {
            String jsonText = JSON.toJSONString(productImageList, true);
            packet.set("img", jsonText);
        }
        packet.set("price", newprice);
        updated(packet);
        redirect("/packet/list");
    }

    @Before({ClearCache.class, Tx.class})
    @J2CacheName(Packet.class)
    public void delete() {
        String[] ids = getParaValues("ids");
        if (ids != null && ids.length > 0) {
            for (String id : ids) {
                PacketProductMap.dao.deleteMap(id);
                if (Packet.dao.delete(id)) {
                    ajaxJsonSuccessMessage("删除成功！");
                } else {
                    ajaxJsonErrorMessage("删除失败！");
                }
            }
        } else {
            ajaxJsonErrorMessage("id为空未选中，删除失败！");
        }
    }

    public List<Shelf> getShelf() {
        return Shelf.dao.getAll();
    }

    public void lookProduct() {
        String packetid = getPara("id");
        Page<Product> pager = getModel(Product.class).paginate(getParaToInt("pageNumber", 1), getParaToInt("pageSize", 10), "select p.id,p.name,p.metaDescription,p.price,p.store ",
                "FROM product p JOIN packetproductmap map ON p.id = map.product_id WHERE map.packet_id = ?", packetid);
        setAttr("pager", pager);
        render("/admin/lookProduct_list.html");
    }

    public void findProductByPage() {
        String select = "select *";
        String sqlExceptSelect = "from product where isDelete = 0 ";
        if (StrKit.notBlank(getProperty()) && StrKit.notBlank(getKeyword())) {
            sqlExceptSelect += "and " + getProperty() + " like '%" + getKeyword() + "%'";
        }
        sqlExceptSelect += " order by createDate desc ";
        Page<Product> pager = Product.dao.paginate(getParaToInt("pageNumber", 1), getParaToInt("pageSize", 10), select, sqlExceptSelect);
        setAttr("pager", pager);
    }

    public void getProduct() {
        String select = "select * ";
        String sqlExceptSelect = " from product where isDelete = 0 ";
        if (getParaToBoolean("_search")) {
            if (StrKit.notBlank(getPara("searchField")) && StrKit.notBlank(getPara("searchString"))) {
                sqlExceptSelect += "and " + getPara("searchField") + " like '%" + getPara("searchString") + "%'";
            }
        }
        sqlExceptSelect += " order by name asc";
        Page<Product> product = Product.dao.paginate(getParaToInt("page", 1), getParaToInt("rows", 5), select, sqlExceptSelect);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("page", product.getPageNumber());
        map.put("total", product.getTotalPage());
        map.put("records", product.getTotalRow());
        map.put("rows", FieldMapUtil.map(product.getList()));
        renderJson(map);
    }

    public List<ProductCategory> getProductCategoryTreeList() {
        return ProductCategory.dao.getProductCategoryTreeList();
    }
}