(ns canboard-frontend.util)

(defn log [s] (.log js/console s))

(defn elem-by-id [id]
  (.getElementById js/document (name id)))

(defn elems-by-class [cls]
  (.getElementsByClassName js/document (name cls)))
