(ns canboard-frontend.lang
  (:require [canboard-frontend.data :as data]))

(def lang-map
  {:en {:user-name "User"
        :password "Password"
        :do-login "Login"
        :do-logout "Logout"
        :session-over "It looks like your session has expired"
        :login-again "Please log in again"
        :do-create "Create"
        :do-cancel "Cancel"
        :title "Title"
        :description "Description"
        :boards {:new "New Board"}}
   })

(defn translate [& keys]
  (get-in lang-map (cons @data/current-language keys)))
