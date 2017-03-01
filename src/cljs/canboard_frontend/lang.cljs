(ns canboard-frontend.lang
  (:require [canboard-frontend.data :as data]))

(def lang-map
  {:en {:user-name "User"
        :password "Password"
        :do-login "Login"}
   })

(defn translate [key]
  (let [path (if (seq? key) key [key])]
    (get-in lang-map (into [(data/current-language)] path))))
