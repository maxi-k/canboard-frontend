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

(defn markdown-render
  [content]
  [:div.md-output
   [:div.markdown-body
    {:dangerouslySetInnerHTML {:__html (js/marked
                                        (str content)
                                        #js {:gfm true
                                             :breaks true
                                             :smartLists true})}}]])

(defn markdown-editor
  [editing toggle-edit-fn cache-atom persistant-atom empty-description wrapper-classes]
  (r/create-class
   :component-did-mount (fn [] (reset! cache-atom @persistant-atom))
   :reagent-render
   (fn []
     (let [editing-toggle (fn [commit] {:on-click #(toggle-edit-fn commit)})]
       [sa/Segment (let [base {:class (str "ui form " wrapper-classes)}]
                     (if @editing base (merge (editing-toggle false) base)))
        (when @editing
          [:div.md-editor
           [:div.md-editor-controls
            [:span.editor-explanation (lang/translate :explanation :markdown)]
            [sa/Button (merge (editing-toggle true) {:class "right floated green"})
             (lang/translate :done)]
            [sa/Button (merge (editing-toggle false) {:class "right floated red"})
             (lang/translate :do-cancel)]
            [:div.clearfloat]]
           [:div.ui.field
            [:textarea.md-input {:value @cache-atom
                                 :placeholder "Markdown"
                                 :on-change #(reset! cache-atom (-> % .-target .-value))}]]])
        [:div.md-output-wrapper.ui.field
         (if @editing (merge (editing-toggle true) {}))
         (if (and (not @editing) (empty? @persistant-atom))
           [:span.grey.text empty-description]
           [markdown-render (if @editing @cache-atom @persistant-atom)])]]))))
