(ns ^:figwheel-always zosimos.core
    (:require
      [om.core :as om]
      [om-tools.core :refer-macros [defcomponent]]
      [om-tools.dom :as dom :include-macros true]
      [sablono.core :as html :refer-macros [html]]))

(enable-console-print!)

(defonce app-state (atom {:text "Hello world!"}))

(defcomponent app [data owner]
  (render [_]
    (html [:div (:text data)])))

(om/root app @app-state
  {:target (. js/document (getElementById "app"))})
