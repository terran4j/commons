
SELECT count(*) from (
    SELECT *, ROUND(6378.137 * 2 * ASIN(SQRT(
        POW(SIN(( @{query.lat} * PI() / 180 - lat * PI() / 180) / 2),2)
        + COS( @{query.lat} * PI() / 180) * COS(lat * PI() / 180)
        * POW(SIN(( @{query.lon} * PI() / 180 - lon * PI() / 180) / 2), 2)
    )) * 1000) AS distance
    FROM test_location
) as t
where t.distance > 1000
