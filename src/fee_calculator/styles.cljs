(ns fee-calculator.styles
  (:require [spade.core :refer [defclass]]
            [garden.selectors :refer [nth-child]]
            [garden.stylesheet :refer [at-media]]))

(defclass height [h]
  {:height (str (.toFixed h 2) "%")})

(defclass print-panel []
  {}
  [:section {:break-inside "avoid"}])

(defclass fee-breakdown-graph []
  {:width "80%"
   :height "100%"
   :margin "0 auto"
   :display "flex"
   :flex-direction "column"
   :justify-content "stretch"
   :align-items "stretch"}

  [:.graph {:flex "1 1 auto"
            :display "flex"
            :justify-content "stretch"}

   [:.label-col {:flex "0 0 auto"
                 :display "flex"
                 :flex-direction "column"
                 :justify-content "space-evenly"
                 :padding-right "0.5em"}

    ['>
     ['* {:flex "1 1 0"
          :display "flex"
          :justify-content "flex-end"}]

     [(nth-child 2) {:align-items "center"}]

     [(nth-child 3) {:align-items "flex-end"}]]]

   [:.portfolio-col {:flex "2 1 0"
                     :display "flex"
                     :flex-direction "column"
                     :justify-content "flex-end"}

    ['>
     ['* {:color "white"
          :display "flex"
          :justify-content "center"
          :align-items "center"
          :text-shadow "black 0 0 10px, 2px 2px 0 black"}]
     
     (at-media {:print true} ['* {:-webkit-print-color-adjust "exact" ; Chrome/Safari/Edge/Opera
                                  :color-adjust "exact" ; Firefox
                                  }])

     [(nth-child 1) {:background-color "#1d1d1b"}]

     [(nth-child 2) {:background-color "#ce1518"}]]]]

  [:.mapping-col {:flex "3 1 0"}

   ['> [:div {:transform "scale(1, -1)"}]]

   [:svg {:padding "0 8px"
          :height "100%"
          :width "100%"}

    [:line {:stroke "black"
            :stroke-dasharray 3}]]]

  [:.fee-col {:flex "2 1 0"
              :display "flex"
              :flex-direction "column"
              :justify-content "flex-end"}
   ['>

    ['* {:color "white"
         :display "flex"
         :justify-content "center"
         :align-items "center"}]
    
    (at-media {:print true} ['* {:-webkit-print-color-adjust "exact" ; Chrome/Safari/Edge/Opera
                                 :color-adjust "exact" ; Firefox
                                 }])

    [(nth-child 1) {:background-color "#b0d2dc"}]

    [(nth-child 2) {:background-color "#1d1d1b"}]

    [(nth-child 3) {:background-color "#ce1518"}]]]

  [:.legend {:display "flex"
             :text-align "center"
             :margin "1em 0"}
   ['>
    [(nth-child 1) {:flex "0 0 auto"
                    :padding-right "0.5em"
                    :visibility "hidden"}]

    [(nth-child 2) {:flex "2 1 0"
                    :font-weight "bold"
                    :visibility "visible"}]

    [(nth-child 3) {:flex "3 1 0"}]

    [(nth-child 4) {:flex "2 1 0"
                    :font-weight "bold"}]]]

  [:.icons {:display "flex"
            :flex-wrap "wrap"
            :justify-content "space-evenly"
            :padding-top "1em"}

   ['> ['* {:display "flex"
            :align-items "center"}]]

   [:span {:padding "0 0.5em"}]])
