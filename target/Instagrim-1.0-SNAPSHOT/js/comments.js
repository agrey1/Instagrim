/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function()
{
    //Display the character allowance for a comment
    $(".postComment").keyup(function()
    {
        $(this).prev(".charCount").text("(" + $(this).val().length + "/100)");
    });
});