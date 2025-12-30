package gr.hua.fitTrack.core.service.model;

public class Create2FARequest {
    private  String phone;
    private String code;
    public Create2FARequest() {
        this.phone = "";
        this.code = "";
    }

    public Create2FARequest(Long personId,String phone, String code){
        this.phone = phone;
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
