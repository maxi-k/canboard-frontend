(ns canboard-frontend.view.parts
  (:require [soda-ash.core :as sa]
            [canboard-frontend.lang :as lang]
            [canboard-frontend.data :as data]
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
   (if (fn? content)
     [content]
     content)])

(defn detail-wrapper [detail-content]
  [:div#detail-content-wrapper
   (if (fn? detail-content)
     [detail-content]
     detail-content)])

(defn default-template
  ([content detail-content]
   [:div#app-wrapper
    [default-header]
    [default-wrapper content]
    [default-footer]
    (when detail-content
      [detail-wrapper detail-content])])

  ([content]
   [default-template content nil]))
