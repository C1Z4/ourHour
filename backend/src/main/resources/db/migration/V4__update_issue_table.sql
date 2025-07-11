ALTER TABLE `tbl_issue` 
ADD COLUMN `project_id` BIGINT NULL,
ADD CONSTRAINT `FK_project_TO_issue` 
FOREIGN KEY (`project_id`) REFERENCES `tbl_project`(`project_id`) ON DELETE SET NULL;

UPDATE `tbl_issue` i 
INNER JOIN `tbl_milestone` m ON i.milestone_id = m.milestone_id
SET i.project_id = m.project_id
WHERE i.project_id IS NULL;

ALTER TABLE `tbl_issue` 
MODIFY COLUMN `milestone_id` BIGINT NULL; 