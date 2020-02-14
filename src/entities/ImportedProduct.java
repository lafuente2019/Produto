/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;


public class ImportedProduct extends Product  {
    private Double customsFee;
    
    public ImportedProduct(){
        super();
    }

    public ImportedProduct(Double customsFree, String name, Double price) {
        super(name, price);
        this.customsFee = customsFree;
    }

    public Double getCustomsFree() {
        return customsFee;
    }

    public void setCustomsFree(Double customsFree) {
        this.customsFee = customsFree;
    }
    @Override
    public String priceTag() {
		return getName() 
				+ " $ " 
				+ String.format("%.2f", totalPrice())
				+ " (Customs fee: $ " 
				+ String.format("%.2f", customsFee)
				+ ")";
	}
    public Double totalPrice(){
        return getPrice() * customsFee; 
    }
}
