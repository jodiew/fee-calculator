(ns fee-calculator.events
  (:require
   [re-frame.core :refer [reg-event-db
                          reg-event-fx]]
   
   [fee-calculator.db :as db]))

;; -- Event Handlers -----------------------------------------------

(reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(reg-event-fx
 ::navigate
 (fn [_ [_ handler]]
   {:navigate handler}))

(reg-event-fx
 ::set-active-panel
 (fn [{:keys [db]} [_ active-panel]]
   {:db (assoc db :active-panel active-panel)}))


(reg-event-db
 ::select-product
 (fn [db [_ product]]
   (assoc db :product product)))

(reg-event-db
 ::investment-amount
 (fn [db [_ amount]]
   (assoc db :investment-amount amount)))

(reg-event-db
 ::allocation-amount
 (fn [db [_ id amount]]
   (assoc-in db [:allocations id :amount] amount)))

(reg-event-db
 ::allocation-percent
 (fn [db [_ id percent]]
   (let [total (:investment-amount db)
         amount (/ (* percent total) 100)]
     (assoc-in db [:allocations id :amount] amount))))

(reg-event-db
 ::handle-next
 (fn [db _]
   (update db :active-step + 1)))

(reg-event-db
 ::handle-back
 (fn [db _]
   (update db :active-step - 1)))

(reg-event-db
 ::search-string
 (fn [db [_ string]]
   (assoc db :search-string string)))

(reg-event-db
 ::admin-fee-option
 (fn [db [_ options]]
   (assoc db :admin-fee-option
          (map (fn [option]
                 {:label (get option "label")
                  :category (get option "category")})
               (js->clj options)))))

(reg-event-db
 ::asset-class-option
 (fn [db [_ options]]
   (assoc db :asset-class-option (js->clj options))))

(reg-event-db
 ::toggle-company-funds
 (fn [db _]
   (update db :open-company-funds not)))

(reg-event-db
 ::toggle-managed-funds
 (fn [db _]
   (update db :open-managed-funds not)))

(reg-event-db
 ::open-securitites
 (fn [db [_ open]]
   (assoc db :open-securities open)))

(reg-event-db
 ::add-allocation
 (fn [db [_ fund]]
   (let [type-id (if (= (:category fund) "COMPANY-FUND")
               :company-ids
               :managed-funds-ids)
         type-name (if (= (:category fund) "COMPANY-FUND")
                     :company-fund
                     :managed-fund-other)]
     (-> db
         (assoc-in [:securities (:issuer-code fund) :in-use?] true)
         (update type-id conj (:issuer-code fund))
         (assoc-in [:allocations (:issuer-code fund)] {:name (:issuer fund)
                                                       :type type-name
                                                       :amount 0})))))

(reg-event-db
 ::remove-allocation
 (fn [db [_ id]]
   (-> db
       (assoc-in [:securities id :in-use?] false)
       (update :company-ids (fn [old-val]
                                 (filter #(not= % id) old-val)))
       (update :managed-funds-ids (fn [old-val]
                                    (filter #(not= % id) old-val)))
       (update :allocations dissoc id))))

(reg-event-db
 ::clear-search
 (fn [db _]
   (-> db
       (assoc :search-string "")
       (assoc :admin-fee-option [])
       (assoc :asset-class-option []))))

(reg-event-db
 ::reset-allocations
 (fn [_ _]
   db/default-db))
