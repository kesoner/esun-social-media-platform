-- 社群媒體平台範例資料 (H2)
-- 用於開發與測試環境

-- 插入範例使用者
-- 注意：實際應用中密碼應使用 BCrypt 加密
INSERT INTO users (id, username, email, password_hash, biography, created_at) VALUES
(1, 'john_doe', 'john@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.Uo0ePPO4tyh.H5E1J3pMMlxlHzDfqe', '熱愛程式設計的軟體工程師', '2024-01-15 10:00:00'),
(2, 'jane_smith', 'jane@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.Uo0ePPO4tyh.H5E1J3pMMlxlHzDfqe', '前端開發者，喜歡創造美好的使用者體驗', '2024-01-16 11:30:00'),
(3, 'mike_chen', 'mike@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.Uo0ePPO4tyh.H5E1J3pMMlxlHzDfqe', '資料科學愛好者', '2024-01-17 09:15:00'),
(4, 'sarah_wang', 'sarah@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.Uo0ePPO4tyh.H5E1J3pMMlxlHzDfqe', 'UI/UX 設計師', '2024-01-18 14:20:00');

-- 插入範例發文
INSERT INTO posts (id, user_id, content, created_at) VALUES
(1, 1, '今天學習了 Spring Boot 的自動配置機制，真的很方便！透過 @EnableAutoConfiguration 註解，Spring Boot 可以根據 classpath 中的依賴自動配置應用程式。', '2024-01-20 09:30:00'),
(2, 2, '分享一個 Vue.js 的小技巧：使用 computed 屬性來處理複雜的資料轉換，不僅程式碼更乾淨，效能也更好。記住，computed 屬性是基於響應式依賴進行快取的！', '2024-01-20 14:15:00'),
(3, 1, '正在研究 SQLite 的效能優化，發現適當的索引設計對查詢速度影響很大。特別是在處理大量資料時，複合索引的使用策略很重要。', '2024-01-21 10:45:00'),
(4, 3, '資料視覺化真的很有趣！今天用 Python 的 matplotlib 製作了一個互動式圖表，可以清楚地看出資料的趨勢變化。', '2024-01-21 16:20:00'),
(5, 4, '設計系統的一致性很重要。建立了一套 Design Token，包含顏色、字體、間距等規範，讓整個產品的視覺體驗更加統一。', '2024-01-22 11:10:00'),
(6, 2, 'RESTful API 設計的最佳實踐：使用適當的 HTTP 方法、狀態碼，以及清晰的資源命名。好的 API 設計讓前後端協作更順暢。', '2024-01-22 15:30:00');

-- 插入範例留言
INSERT INTO comments (id, user_id, post_id, content, created_at) VALUES
(1, 2, 1, '同意！Spring Boot 的自動配置真的省了很多設定時間，讓開發者可以專注在業務邏輯上。', '2024-01-20 10:15:00'),
(2, 3, 1, '有沒有推薦的 Spring Boot 學習資源？我也想深入了解。', '2024-01-20 11:00:00'),
(3, 1, 2, 'Vue.js 的 computed 確實很強大，我之前都用 methods，現在知道差別了！', '2024-01-20 15:30:00'),
(4, 4, 2, '補充一點：computed 屬性只有在相關響應式依賴發生改變時才會重新求值，這就是為什麼效能更好的原因。', '2024-01-20 16:45:00'),
(5, 2, 3, 'SQLite 的索引設計確實很重要，可以分享一些具體的優化案例嗎？', '2024-01-21 12:20:00'),
(6, 3, 4, '資料視覺化是我的興趣！有機會可以交流一下 Python 的資料分析技巧。', '2024-01-21 17:10:00'),
(7, 1, 5, 'Design Token 的概念很棒，這樣可以確保設計的一致性，也方便維護。', '2024-01-22 12:30:00'),
(8, 4, 6, 'API 設計真的很重要，清楚的文件和一致的命名規範可以大大提升開發效率。', '2024-01-22 16:15:00'),
(9, 3, 6, '推薦使用 OpenAPI (Swagger) 來文件化 API，自動生成的文件很方便。', '2024-01-22 17:00:00'),
(10, 1, 4, 'matplotlib 很強大，不過我最近在學 D3.js，想做更互動式的視覺化。', '2024-01-23 09:20:00');

-- H2 會自動處理序列值，無需手動更新
