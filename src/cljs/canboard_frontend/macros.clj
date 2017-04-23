(ns canboard-frontend.macros)

(defmacro defpartial
  [name orig-fn argv obj]
  `(defn ~name ~argv
     (~orig-fn ~obj ~@argv)))

(defmacro defmap
  [name doc pre-post-map & defs]
  (let [defmap (reduce (fn [coll [k v]] (assoc coll k (cons 'fn v))) {} (apply hash-map defs))
        id (fn [& args] args)
        pre-fn (or (get pre-post-map :pre) id)
        post-fn (or (get pre-post-map :post) id)]
    `(defn ~name ~doc [key# & args#]
       (when-let [fn# (get ~defmap key#)]
         (let [pre# (apply ~pre-fn key# args#)
               res# (apply fn# pre#)]
           (apply post-fn key# pre#)
           res#)))))
