package cn.kingtous.downloadit.Model;

import java.util.List;

public class fcgModel {


    /**
     * code : 0
     * cid : 205361747
     * userip : 221.192.180.240
     * data : {"expiration":80400,"items":[{"subcode":0,"songmid":"0039MnYb0qxYhV","filename":"C4000039MnYb0qxYhV.m4a","vkey":"DA1305BB4571D83CC17E797E37E951E7AB0FD9AE2E91C7EAF380D4B1ADED14C7A98CDA384F5A3F4AFC079BFC0C094695440FADFA4C33A34D"}]}
     */

    private int code;
    private int cid;
    private String userip;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getUserip() {
        return userip;
    }

    public void setUserip(String userip) {
        this.userip = userip;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * expiration : 80400
         * items : [{"subcode":0,"songmid":"0039MnYb0qxYhV","filename":"C4000039MnYb0qxYhV.m4a","vkey":"DA1305BB4571D83CC17E797E37E951E7AB0FD9AE2E91C7EAF380D4B1ADED14C7A98CDA384F5A3F4AFC079BFC0C094695440FADFA4C33A34D"}]
         */

        private int expiration;
        private List<ItemsBean> items;

        public int getExpiration() {
            return expiration;
        }

        public void setExpiration(int expiration) {
            this.expiration = expiration;
        }

        public List<ItemsBean> getItems() {
            return items;
        }

        public void setItems(List<ItemsBean> items) {
            this.items = items;
        }

        public static class ItemsBean {
            /**
             * subcode : 0
             * songmid : 0039MnYb0qxYhV
             * filename : C4000039MnYb0qxYhV.m4a
             * vkey : DA1305BB4571D83CC17E797E37E951E7AB0FD9AE2E91C7EAF380D4B1ADED14C7A98CDA384F5A3F4AFC079BFC0C094695440FADFA4C33A34D
             */

            private int subcode;
            private String songmid;
            private String filename;
            private String vkey;

            public int getSubcode() {
                return subcode;
            }

            public void setSubcode(int subcode) {
                this.subcode = subcode;
            }

            public String getSongmid() {
                return songmid;
            }

            public void setSongmid(String songmid) {
                this.songmid = songmid;
            }

            public String getFilename() {
                return filename;
            }

            public void setFilename(String filename) {
                this.filename = filename;
            }

            public String getVkey() {
                return vkey;
            }

            public void setVkey(String vkey) {
                this.vkey = vkey;
            }
        }
    }
}
