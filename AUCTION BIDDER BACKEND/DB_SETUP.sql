-- Create a simple geo_samples table and insert sample JSON-like records
CREATE TABLE IF NOT EXISTS geo_samples (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  body VARCHAR(255),
  area VARCHAR(255),
  rock_type VARCHAR(100)
);

INSERT INTO geo_samples (body, area, rock_type) VALUES
('SampleSite1','North Ridge','Igneous'),
('SampleSite2','South Valley','Sedimentary'),
('SampleSite3','East Cliff','Metamorphic');

-- Verify data
SELECT * FROM geo_samples;

-- Example JSON dataset (for imports or API payloads)
-- Save this snippet as samples.json if you want to POST it to a custom endpoint
-- [
--   {"body":"SampleSite1","area":"North Ridge","rockType":"Igneous"},
--   {"body":"SampleSite2","area":"South Valley","rockType":"Sedimentary"},
--   {"body":"SampleSite3","area":"East Cliff","rockType":"Metamorphic"}
-- ]
