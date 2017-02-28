(ns canboard-frontend.views
  (:require [canboard-frontend.data :as data]))

(defn navigation []
  [:div#navigation
   [:a {:href "/"} "Home"]
   [:a {:href "/about"} "About"]])

(defn title []
  [:span
   [:span#title-thick "Can"]
   [:span#title-thin "board"]])

(defn default-header []
  [:div#navbar
   [:a {:href "/"}
    [:div#navbar-logo]
    [:div#navbar-title (title)]]
   (navigation)
   [:div#clearfloat]])

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

(defn home-page [state]
  (default-template
   [:div
    [:h2 (title)]
    [:h2 "Welcome"]]))

(defn about-page [state]
  (default-template
   [:div
    [:h2 (title)]
    [:h2 "About"]]))
