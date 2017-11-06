
SELECT *, ROUND(6378.137 * 2 * ASIN(SQRT(
    POW(SIN(( @{lat} * PI() / 180 - lat * PI() / 180) / 2),2)
    + COS( @{lat} * PI() / 180) * COS(lat * PI() / 180)
    * POW(SIN(( @{lon} * PI() / 180 - lon * PI() / 180) / 2), 2)
)) * 1000) AS distance
FROM test_location
ORDER BY distance ASC
limit 0, 1