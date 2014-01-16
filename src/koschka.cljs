(ns koschka
  (:require [strokes :refer [d3]]))

(strokes/bootstrap)

(def margin { :top 20 :bottom 20
              :left 20 :right 40
              })
(def width (- 1024 (:left margin) (:right margin)))
(def height (- 768 (:top margin) (:bottom margin)))



(def data-url "https://docs.google.com/spreadsheet/pub?key=0AiGB19Qe-0B2dF95YjhseDZPVUlMUmcxNTRJcFA5S3c&output=csv")

(defn show-status [text]
  (-> d3 (.select "#status") (.html text)))



(def vis (-> d3 (.select "body")
             (.append "svg")
               (.attr {:width (+ width (:left margin) (:right margin))
                       :height (+ height (:top margin) (:bottom margin))})
             (.append "g")
               (.attr {:class "vis"
                       :transform (str "translate(" (:left margin) "," (:top margin) ")")})))



(def x (-> d3/time .scale
           (.range (array 0 width))))
(def y (-> d3/scale .linear
           (.range (array height 0))))


(def x-axis (-> d3/svg .axis (.scale x) (.orient "bottom")))

(defn parse-time [str]
  (let [time-format (-> d3/time (.format "%d.%m.%Y %H:%M"))]
  (.parse time-format str)))


(let [hour-fmt (-> d3/time (.format "%H:%M"))]
(defn get-hour [date] (hour-fmt date)))


(defn prepare-data [data]
  (let [prepare-entry (fn [entry] {
                          :date  (parse-time (aget entry "date"))
                          :sugar  (js/parseFloat (aget entry "sugar")) })]

    (->> data (map prepare-entry) (filter :date) clj->js)))


(defn render [data]
  (show-status "")


  (let [latest-date   (d3/max data #(aget % "date"))
        start         (-> d3 .-time .-day (.offset latest-date -1))]
    (-> x
        ;(.domain (d3/extent data #(aget % "date") ))))
          (.domain (array start latest-date))))

  (-> y
      (.domain (array 0 (d3/max data #(aget % "sugar") ))))


  (-> vis (.append "g")
      (.attr "class" "x axis")
      (.attr "transform" (str "translate(0," height ")"))
      (.call x-axis))

  (let [g (-> vis
            (.selectAll ".probe")
              (.data data)
            (.enter)
            (.append "g")
              (.attr "class" "probe")
              (.attr "transform" #(str "translate(" (x (aget % "date")) "," (y (aget % "sugar")) ")") ))]

    (-> g
        (.append "circle")
          (.attr "r" 5))

    (-> g
        (.append "text")
          (.attr "class" "hour")
          (.attr "x" +7)
          (.attr "y" 3)
          (.text #(get-hour (aget % "date"))))
    (-> g
        (.append "text")
          (.attr "class" "sugar")
          (.attr "y" 15)
          (.text #(aget % "sugar")))

    ))


(-> d3 (.csv data-url (fn [err, data]
                        (if err
                             (show-status "Couldn't load data")
                             (render (prepare-data data))))))
