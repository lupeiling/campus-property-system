package com.campus.property.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.property.dto.PurchaseApplyDTO;
import com.campus.property.entity.*;
import com.campus.property.mapper.*;
import com.campus.property.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MaterialService {

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private MaterialCategoryMapper categoryMapper;

    @Autowired
    private MaterialPurchaseMapper purchaseMapper;

    @Autowired
    private MaterialPurchaseItemMapper purchaseItemMapper;

    @Autowired
    private MaterialUsageMapper usageMapper;

    @Autowired
    private UserMapper userMapper;

    public IPage<Material> listMaterials(int current, int size, String keyword, Long categoryId) {
        Page<Material> page = new Page<>(current, size);
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Material::getMaterialName, keyword)
                    .or().like(Material::getMaterialNo, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(Material::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Material::getCreateTime);
        return materialMapper.selectPage(page, wrapper);
    }

    public List<Material> listAllMaterials() {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Material::getCreateTime);
        return materialMapper.selectList(wrapper);
    }

    public void addMaterial(Material material) {
        materialMapper.insert(material);
    }

    public void updateMaterial(Material material) {
        materialMapper.updateById(material);
    }

    public void deleteMaterial(Long id) {
        materialMapper.deleteById(id);
    }

    public List<MaterialCategory> listCategories() {
        LambdaQueryWrapper<MaterialCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(MaterialCategory::getId);
        return categoryMapper.selectList(wrapper);
    }

    public void addCategory(MaterialCategory category) {
        categoryMapper.insert(category);
    }

    @Transactional
    public void applyPurchase(PurchaseApplyDTO dto, Long applicantId) {
        MaterialPurchase purchase = new MaterialPurchase();
        purchase.setPurchaseNo("PO" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        purchase.setApplicantId(applicantId);
        purchase.setTitle(dto.getTitle());
        purchase.setStatus(0);

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurchaseApplyDTO.PurchaseItemDTO item : dto.getItems()) {
            BigDecimal subtotal = item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
        }
        purchase.setTotalAmount(totalAmount);
        purchaseMapper.insert(purchase);

        for (PurchaseApplyDTO.PurchaseItemDTO item : dto.getItems()) {
            MaterialPurchaseItem purchaseItem = new MaterialPurchaseItem();
            purchaseItem.setPurchaseId(purchase.getId());
            purchaseItem.setMaterialId(item.getMaterialId());
            purchaseItem.setQuantity(item.getQuantity());
            purchaseItem.setUnitPrice(item.getUnitPrice());
            purchaseItem.setSubtotal(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
            purchaseItem.setRemark(item.getRemark());
            purchaseItemMapper.insert(purchaseItem);
        }
    }

    public IPage<MaterialPurchase> listPurchases(int current, int size, Integer status) {
        Page<MaterialPurchase> page = new Page<>(current, size);
        LambdaQueryWrapper<MaterialPurchase> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(MaterialPurchase::getStatus, status);
        }
        wrapper.orderByDesc(MaterialPurchase::getCreateTime);
        IPage<MaterialPurchase> result = purchaseMapper.selectPage(page, wrapper);
        for (MaterialPurchase p : result.getRecords()) {
            if (p.getApplicantId() != null) {
                User applicant = userMapper.selectById(p.getApplicantId());
                if (applicant != null) {
                    p.setApplicantName(applicant.getRealName() != null ? applicant.getRealName() : applicant.getUsername());
                }
            }
        }
        return result;
    }

    public List<MaterialPurchaseItem> getPurchaseItems(Long purchaseId) {
        LambdaQueryWrapper<MaterialPurchaseItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialPurchaseItem::getPurchaseId, purchaseId);
        return purchaseItemMapper.selectList(wrapper);
    }

    @Transactional
    public void approvePurchase(Long purchaseId, Long approveId, Integer status, String remark) {
        MaterialPurchase purchase = purchaseMapper.selectById(purchaseId);
        if (purchase == null) {
            throw new RuntimeException("采购单不存在");
        }
        purchase.setStatus(status);
        purchase.setApproveId(approveId);
        purchase.setApproveTime(LocalDateTime.now());
        purchase.setApproveRemark(remark);

        if (status == 1) {
            List<MaterialPurchaseItem> items = getPurchaseItems(purchaseId);
            for (MaterialPurchaseItem item : items) {
                Material material = materialMapper.selectById(item.getMaterialId());
                if (material != null) {
                    material.setStockQuantity(material.getStockQuantity() + item.getQuantity());
                    materialMapper.updateById(material);
                }
            }
        }
        purchaseMapper.updateById(purchase);
    }

    public IPage<MaterialUsage> listUsages(int current, int size, Long materialId, Long userId) {
        Page<MaterialUsage> page = new Page<>(current, size);
        LambdaQueryWrapper<MaterialUsage> wrapper = new LambdaQueryWrapper<>();
        if (materialId != null) {
            wrapper.eq(MaterialUsage::getMaterialId, materialId);
        }
        if (userId != null) {
            wrapper.eq(MaterialUsage::getUserId, userId);
        }
        wrapper.orderByDesc(MaterialUsage::getCreateTime);
        return usageMapper.selectPage(page, wrapper);
    }

    @Transactional
    public void useMaterial(Long materialId, Long userId, Integer quantity, String purpose) {
        Material material = materialMapper.selectById(materialId);
        if (material == null) {
            throw new RuntimeException("物资不存在");
        }
        if (material.getStockQuantity() < quantity) {
            throw new RuntimeException("库存不足");
        }
        material.setStockQuantity(material.getStockQuantity() - quantity);
        materialMapper.updateById(material);

        MaterialUsage usage = new MaterialUsage();
        usage.setMaterialId(materialId);
        usage.setUserId(userId);
        usage.setQuantity(quantity);
        usage.setPurpose(purpose);
        usage.setStatus(1);
        usageMapper.insert(usage);
    }

    @Transactional
    public void returnMaterial(Long usageId) {
        MaterialUsage usage = usageMapper.selectById(usageId);
        if (usage == null) {
            throw new RuntimeException("领用记录不存在");
        }
        if (usage.getStatus() == 0) {
            throw new RuntimeException("该物资已归还");
        }
        usage.setStatus(0);
        usageMapper.updateById(usage);

        Material material = materialMapper.selectById(usage.getMaterialId());
        if (material != null) {
            material.setStockQuantity(material.getStockQuantity() + usage.getQuantity());
            materialMapper.updateById(material);
        }
    }
}
