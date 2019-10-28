(ns magnet.cms.core)

(defprotocol E-Commerce
  (get-all-orders [this])
  (get-order [this order-id])
  (update-order [this order-id fields])
  (fulfill-order [this order-id])
  (unfulfill-order [this order-id])
  (refund-order [this order-id])
  (get-item-inventory [this collection-id item-id])
  (update-item-inventory [this collection-id item-id fields]))

(defprotocol Collections
  (get-all-collections [this])
  (get-collection [this collection-id]))

(defprotocol Items
  (get-items [this collection-id])
  (get-item [this collection-id item-id]))
