(ns ^:figwheel-always zosimos.core
    (:require
      [om.core :as om]
      [om-tools.core :refer-macros [defcomponent defcomponentmethod]]
      [om-tools.dom :as dom :include-macros true]
      [sablono.core :as html :refer-macros [html]]))

(enable-console-print!)

(defonce app-state
  (atom
    {:meta {}
     :layout
     {:grid-columns 4
      :component-sort [[1][2 3][4][5][7 8]]}
     :components [{:type :title
                   :id 1
                   :user-data
                   {:title "This is the page title."}
                   :section-affinity ["header"]}
                  {:type :content
                   :id 2
                   :user-data
                   {:content "This is user content."}}
                  {:type :image
                   :id 3
                   :user-data
                   {:src "https://www.dropbox.com/s/0q37qjigyxtaofh/Screenshot%202015-04-24%2000.12.54.png?dl=1"
                    :alt "Monkey business"}}
                  {:type :embedly
                   :id 4
                   :user-data nil}
                  {:type :iframe
                   :id 5
                   :user-data
                   {:src "http://www.promojam.com"
                    :width "600px"
                    :height "200px"}}
                  {:type :facebook-like-button
                   :id 7
                   :user-data nil}
                  {:type :form
                   :id 8
                   :user-data nil}]}))

(defmulti widget
  "The most basic feature component that can be added to a layout."
  (fn [data owner] (:type data)))

(defcomponentmethod widget :title
  [data owner]
  (render [_]
    (html [:h1.widget.title-widget
           (-> data :user-data :title)
           [:h2 "hi"]])))

(defcomponentmethod widget :content
  [data owner]
  (render [_]
    (html [:div.widget.content-widget
           (-> data :user-data :content)])))

(defcomponentmethod widget :image
  [data owner]
  (render [_]
    (html [:div.widget.image-widget
           (html/image (-> data :user-data :src)
                       (-> data :user-data :alt))])))

(defcomponentmethod widget :iframe
  [data owner]
  (render [_]
    (html [:div.widget.image-widget
           [:iframe {:src (-> data :user-data :src)}]])))

(defcomponentmethod widget :default
  [data owner]
  (render [_]
    (html [:div.widget.default-widget {:class (name (:type data))}
           [:div.title (str "Widget " (:type data))]])))

(defcomponent app
  "The main application component."
  [data owner]
  (render [_]
    (html
      [:div {:class "app"}
       (om/build-all widget (:components data))])))

(om/root app @app-state
  {:target (. js/document (getElementById "app"))})
