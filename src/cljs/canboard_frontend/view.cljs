(ns canboard-frontend.view
  (:require [canboard-frontend.data :as data]
            [canboard-frontend.lang :as lang :refer [translate]]
            [soda-ash.core :as sa]))

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
   [:a.item {:href "/"} "Home"]
   [:a.item {:href "/about"} "About"]
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

(defn home-page []
  (default-template
   [:div
    [:h2 (title)]
    [:h2 "Welcome"]]))

(defn about-page []
  (default-template
   [:div
    [:h2 (title)]
    [:h2 "About"]]))

(defn login-page []
  [:div {:class "ui grid centered equal width" :id :login-segment}
   [sa/Segment {:class "twelve"}
    [sa/Image {:size :small
               :class "ui centered"
               :src "img/logo/logo-large.png"}]
    [:div#big-title (title)]
    [sa/Form
     [:div.field
      [:label (translate :user-name)]
      [:input {:type :text :name :user-name :placeholder "test@example.com"}]]
     [:div.field
      [:label (translate :password)]
      [:input {:type :password :name :password :placeholder "password"}]]
     ]]])
