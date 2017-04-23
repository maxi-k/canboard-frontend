(ns canboard-frontend.lang
  (:require [canboard-frontend.data :as data]))

(def lang-map
  {:en {:user-name "User"
        :password "Password"
        :do-login "Login"
        :do-logout "Logout"
        :session-over "It looks like your session has expired"
        :login-again "Please log in again"
        :wrong-credentials "It looks like your email or password were incorrect"
        :do-create "Create"
        :do-edit "Edit"
        :do-cancel "Cancel"
        :title "Title"
        :done "Done"
        :description "Description"
        :description/not-found "There's no description yet. Click here to add one."
        :explanation {:markdown "You can use markdown to format text."}
        :confirm {:deletion "Really Delete?"}
        :boards {:new "New Board"
                 :delete "Delete Board"}
        :lists {:new "New List"
                :delete "Delete List"}
        :cards {:new "New Card"
                :delete "Delete Card"
                :title "Card Title"}}})

(defn translate [& keys]
  (get-in lang-map (cons @data/current-language keys)))
