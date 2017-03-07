(ns canboard-frontend.util)

(def relevant-auth-headers [:access-token :token-type :client :uid :expiry])
(def relevant-auth-headers-str (map name relevant-auth-headers))

(defn log [s] (.log js/console s))

(defn elem-by-id [id]
  (.getElementById js/document (name id)))

(defn elems-by-class [cls]
  (.getElementsByClassName js/document (name cls)))
