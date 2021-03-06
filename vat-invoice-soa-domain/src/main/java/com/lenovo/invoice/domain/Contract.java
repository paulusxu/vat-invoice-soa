package com.lenovo.invoice.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Contract  implements Serializable {

    private Long id;

    private String contractNo;

    private String buyerName;

    private String shipName;

    private String shipMobile;

    private String shipAddress;

    private Date createTime;

    private Date takeffectTime;

    private String lenovoId;

    private int issend;

    private String url;

    private String orderCode;

    private int migrationType;

    private String takeffectTimeStr;


    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public int getMigrationType() {
        return migrationType;
    }

    public void setMigrationType(int migrationType) {
        this.migrationType = migrationType;
    }

    public String getTakeffectTimeStr() {
        if(takeffectTime!=null){
            return format.format(takeffectTime);
        }
        return takeffectTimeStr;
    }

    public void setTakeffectTimeStr(String takeffectTimeStr) {
        this.takeffectTimeStr = takeffectTimeStr;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column contract.id
     *
     * @return the value of contract.id
     * @mbggenerated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column contract.id
     *
     * @param id the value for contract.id
     * @mbggenerated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column contract.contract_no
     *
     * @return the value of contract.contract_no
     * @mbggenerated
     */
    public String getContractNo() {
        return contractNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column contract.contract_no
     *
     * @param contractNo the value for contract.contract_no
     * @mbggenerated
     */
    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public Date getTakeffectTime() {
        return takeffectTime;
    }

    public void setTakeffectTime(Date takeffectTime) {
        this.takeffectTime = takeffectTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column contract.ship_name
     *
     * @return the value of contract.ship_name
     * @mbggenerated
     */
    public String getShipName() {
        return shipName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column contract.ship_name
     *
     * @param shipName the value for contract.ship_name
     * @mbggenerated
     */
    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column contract.ship_mobile
     *
     * @return the value of contract.ship_mobile
     * @mbggenerated
     */
    public String getShipMobile() {
        return shipMobile;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column contract.ship_mobile
     *
     * @param shipMobile the value for contract.ship_mobile
     * @mbggenerated
     */
    public void setShipMobile(String shipMobile) {
        this.shipMobile = shipMobile;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column contract.ship_address
     *
     * @return the value of contract.ship_address
     * @mbggenerated
     */
    public String getShipAddress() {
        return shipAddress;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column contract.ship_address
     *
     * @param shipAddress the value for contract.ship_address
     * @mbggenerated
     */
    public void setShipAddress(String shipAddress) {
        this.shipAddress = shipAddress;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column contract.create_time
     *
     * @return the value of contract.create_time
     * @mbggenerated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column contract.create_time
     *
     * @param createTime the value for contract.create_time
     * @mbggenerated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column contract.lenovo_id
     *
     * @return the value of contract.lenovo_id
     * @mbggenerated
     */
    public String getLenovoId() {
        return lenovoId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column contract.lenovo_id
     *
     * @param lenovoId the value for contract.lenovo_id
     * @mbggenerated
     */
    public void setLenovoId(String lenovoId) {
        this.lenovoId = lenovoId;
    }

    public int getIssend() {
        return issend;
    }

    public void setIssend(int issend) {
        this.issend = issend;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }
}