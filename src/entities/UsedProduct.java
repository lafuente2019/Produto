/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.util.Date;


public class UsedProduct extends Product{
    
    private Date manufatureDate ;
    
    public UsedProduct(){
        super();
    }

    public UsedProduct(Date manufatureDate, String name, Double price) {
        super(name, price);
        this.manufatureDate = manufatureDate;
    }

    public Date getManufatureDate() {
        return manufatureDate;
    }

    public void setManufatureDate(Date manufatureDate) {
        this.manufatureDate = manufatureDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
    public void priceTag(){
        
    }
    
}
