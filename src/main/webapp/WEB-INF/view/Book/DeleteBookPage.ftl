<#import "../parts/AskUser.ftl" as askUser>
<@askUser.page 
"DeleteBook" 
"Are you sure you want to delete book ?" 
"../DeleteBook/${Book_id}"
/>