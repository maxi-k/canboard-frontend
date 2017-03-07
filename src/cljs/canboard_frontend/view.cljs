(ns canboard-frontend.view
  (:require [canboard-frontend.data :as data]
            [canboard-frontend.lang :as lang :refer [translate]]
            [canboard-frontend.api :as api]
            [canboard-frontend.util :as util]
            [reagent.core :as r]
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
   [:a.item {:href "/boards"} "Boards"]
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
    [:h2 "Welcome"]
    [:div [:p] [:h4 "User Data"]
     [:p
      (str @data/current-user)]]]))

(defn boards-page []
  (r/create-class
   {:component-will-mount api/fetch-boards
    :display-name "boards-page"
    :reagent-render
    (fn []
      (default-template
       (str @data/boards)
       ))}))

(def login-page
  (letfn [(auth []
            (api/authenticate-user! (.-value (util/elem-by-id :login-username))
                                    (.-value (util/elem-by-id :login-password))))
          (key-callback [e] (when (= 13 (.-charCode e)) (auth)))
          (btn-callback [e] (auth))]
    (fn []
      [:div {:class "ui grid centered equal width" :id :login-segment}
       [sa/Segment {:class "twelve"}
        [sa/Image {:size :small
                   :class "ui centered"
                   :src "img/logo/logo-large.png"}]
        [:div#big-title (title)]
        [:div.ui.form
         [:div.field
          [:label (translate :user-name)]
          [:input {:id :login-username :type :text :name :user-name :placeholder "test@example.com"
                   :on-key-press key-callback}]]
         [:div.field
          [:label (translate :password)]
          [:input {:id :login-password :type :password :name :password :placeholder "password"
                   :on-key-press key-callback}]]
         [sa/Button {:on-click btn-callback}
          (translate :do-login)
          ]
         ]]])))
