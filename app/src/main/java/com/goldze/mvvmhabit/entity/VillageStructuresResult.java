package com.goldze.mvvmhabit.entity;

/**
 * @Author: zhouxiaolin
 * @CreateDate: 2020/6/2 18:04
 * @Description: 3.1	逐级获取区域结构
 */
public class VillageStructuresResult {
    //小区节点ID
    private int StructureID;
    //父节点ID
    private int ParentStructureID;
    //小区节点编号
    private String StructureDirectory;
    //属性（1、小区 2、围墙 3、单元门 10、房屋）
    private int Attribute;
    //小区节点名称（设备界面上以此作为每一级节点名称的显示）
    private String StructureName;
    //附加属性（1、别墅）
    private int ExtraAttribute;

    public int getStructureID() {
        return StructureID;
    }

    public void setStructureID(int structureID) {
        StructureID = structureID;
    }

    public int getParentStructureID() {
        return ParentStructureID;
    }

    public void setParentStructureID(int parentStructureID) {
        ParentStructureID = parentStructureID;
    }

    public String getStructureDirectory() {
        return StructureDirectory;
    }

    public void setStructureDirectory(String structureDirectory) {
        StructureDirectory = structureDirectory;
    }

    public int getAttribute() {
        return Attribute;
    }

    public void setAttribute(int attribute) {
        Attribute = attribute;
    }

    public String getStructureName() {
        return StructureName;
    }

    public void setStructureName(String structureName) {
        StructureName = structureName;
    }

    public int getExtraAttribute() {
        return ExtraAttribute;
    }

    public void setExtraAttribute(int extraAttribute) {
        ExtraAttribute = extraAttribute;
    }
}
