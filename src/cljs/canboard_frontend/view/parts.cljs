(ns canboard-frontend.view.parts
  (:require [soda-ash.core :as sa]
            [canboard-frontend.lang :as lang]
            [canboard-frontend.api :as api]))

(defn title []
  [:span
   [:span#title-thick "Can"]
   [:span#title-thin "board"]])

(defn default-header []
  [sa/Menu {:class :top :id :navbar}
   [sa/Header {:class :item}
    [:a {:href "/"}
     [:div#navbar-logo]
     [:div#navbar-title (title)]
     [:div.clearfloat]]]
   [:a.item {:href "/boards"} "Boards"]
   [:a.item {:class "floated right"
             :href "/"
             :on-click api/logout!}
    [:span
     (lang/translate :do-logout)]]
   [:div.clearfloat]])

(defn default-footer []
  [:div#footer])

(defn default-wrapper [content]
  [:div#app-content
   content])

(defn default-template [content]
  [:div#app-wrapper
   (default-header)
   (default-wrapper content)
   (default-footer)])
