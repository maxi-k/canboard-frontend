(ns canboard-frontend.util)

(def relevant-auth-headers [:access-token :token-type :client :uid :expiry])
(def relevant-auth-headers-str (map name relevant-auth-headers))

(defn log [s] (.log js/console s))

(defn elem-by-id [id]
  (.getElementById js/document (name id)))

(defn elems-by-class [cls]
  (.getElementsByClassName js/document (name cls)))

(defn current-path
  "Returns the current path of the window."
  []
  (-> js/window .-location .-pathname))

(defn seq-to-map
  "Converts a collection to a map by applying given function key-fn
  to each element and using the result as key for the result of
  applying val-fn to the element."
  [key-fn val-fn coll]
  (reduce (fn [xs x] (assoc xs (key-fn x) (val-fn x)))
          {}
          coll))
