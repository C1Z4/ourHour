ALTER TABLE `tbl_user`
    ADD COLUMN `social_access_token` VARCHAR(2048) NULL AFTER `oauth_id`;
