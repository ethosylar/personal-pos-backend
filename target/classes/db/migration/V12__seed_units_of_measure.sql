INSERT INTO lt_units_uom (
    code,
    name,
    symbol,
    measurement_type,
    is_base_unit,
    display_order
)
VALUES

    (
        'G',
        'Gram',
        'g',
        'MASS',
        TRUE,
        10
    ),

    (
        'KG',
        'Kilogram',
        'kg',
        'MASS',
        FALSE,
        20
    ),

    (
        'ML',
        'Millilitre',
        'ml',
        'VOLUME',
        TRUE,
        30
    ),

    (
        'L',
        'Litre',
        'L',
        'VOLUME',
        FALSE,
        40
    ),

    (
        'PCS',
        'Piece',
        'pcs',
        'COUNT',
        TRUE,
        50
    ),

    (
        'PACK',
        'Pack',
        'pack',
        'PACKAGE',
        FALSE,
        60
    ),

    (
        'BAG',
        'Bag',
        'bag',
        'PACKAGE',
        FALSE,
        70
    ),

    (
        'BOTTLE',
        'Bottle',
        'btl',
        'PACKAGE',
        FALSE,
        80
    ),

    (
        'CAN',
        'Can',
        'can',
        'PACKAGE',
        FALSE,
        90
    ),

    (
        'BOX',
        'Box',
        'box',
        'PACKAGE',
        FALSE,
        100
    )

ON CONFLICT (code)
DO NOTHING;