
UPDATE demo_address SET `name` = @{name}
WHERE id IN (
    SELECT t.id FROM (
        SELECT *, ROUND(6378.137 * 2 * ASIN(SQRT(
            POW(SIN(( @{lat} * PI() / 180 - lat * PI() / 180) / 2),2)
            + COS( @{lat} * PI() / 180) * COS(lat * PI() / 180)
            * POW(SIN(( @{lon} * PI() / 180 - lon * PI() / 180) / 2), 2)
        )) * 1000) AS distance
        FROM demo_address
        ORDER BY distance ASC
        LIMIT 0, 1
    ) AS t
)