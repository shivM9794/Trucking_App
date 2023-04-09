package osattransport.com.osatdriver.models;

/**
 * Created by MAC2 on 03-Mar-18.
 */

public class DriverDetails {
    String id, transporterid, name, address, city, phone, photopath, licensepath, isactive, regdate, totalOrders, approvedOrders, unApprovedOrders;

    public String getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(String totalOrders) {
        this.totalOrders = totalOrders;
    }

    public String getApprovedOrders() {
        return approvedOrders;
    }

    public void setApprovedOrders(String approvedOrders) {
        this.approvedOrders = approvedOrders;
    }

    public String getUnApprovedOrders() {
        return unApprovedOrders;
    }

    public void setUnApprovedOrders(String unApprovedOrders) {
        this.unApprovedOrders = unApprovedOrders;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransporterid() {
        return transporterid;
    }

    public void setTransporterid(String transporterid) {
        this.transporterid = transporterid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotopath() {
        return photopath;
    }

    public void setPhotopath(String photopath) {
        this.photopath = photopath;
    }

    public String getLicensepath() {
        return licensepath;
    }

    public void setLicensepath(String licensepath) {
        this.licensepath = licensepath;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }
}
