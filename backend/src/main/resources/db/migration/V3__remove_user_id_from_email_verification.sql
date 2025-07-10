ALTER TABLE `tbl_email_verification`
DROP FOREIGN KEY `FK_user_TO_email_verification`;

ALTER TABLE `tbl_email_verification`
DROP COLUMN user_id;
