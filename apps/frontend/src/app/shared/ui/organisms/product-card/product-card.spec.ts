import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { Product } from '../../../../features/catalog/data-access/product.model';
import { QuantityStepper } from '../../molecules/quantity-stepper/quantity-stepper';
import { ProductCard } from './product-card';

describe('ProductCard', () => {
  let fixture: ComponentFixture<ProductCard>;

  const product: Product = {
    id: 'p1',
    name: 'Producto Tecnología',
    description: 'Un producto de categoría Tecnología',
    unitPrice: 50,
    category: 'TECNOLOGIA',
    stock: 3,
  };

  beforeEach(() => {
    fixture = TestBed.createComponent(ProductCard);
    fixture.componentRef.setInput('product', product);
  });

  it('should not mark the card as in-cart when quantity is 0', () => {
    fixture.componentRef.setInput('quantity', 0);
    fixture.detectChanges();

    const article = (fixture.nativeElement as HTMLElement).querySelector('article');
    expect(article?.classList.contains('product-card--in-cart')).toBe(false);
  });

  it('should mark the card as in-cart once a quantity greater than 0 is present', () => {
    fixture.componentRef.setInput('quantity', 1);
    fixture.detectChanges();

    const article = (fixture.nativeElement as HTMLElement).querySelector('article');
    expect(article?.classList.contains('product-card--in-cart')).toBe(true);
  });

  it('should pass the product stock as the ceiling for the nested quantity stepper', () => {
    fixture.componentRef.setInput('quantity', 1);
    fixture.detectChanges();

    const stepper = fixture.debugElement.query(By.directive(QuantityStepper))
      .componentInstance as QuantityStepper;

    expect(stepper.max()).toBe(product.stock);
    expect(stepper.quantity()).toBe(1);
  });

  it('should emit increment when the nested stepper requests an increment', () => {
    fixture.componentRef.setInput('quantity', 0);
    fixture.detectChanges();

    const incrementSpy = vi.fn();
    fixture.componentInstance.increment.subscribe(incrementSpy);

    const stepper = fixture.debugElement.query(By.directive(QuantityStepper))
      .componentInstance as QuantityStepper;
    stepper.increment.emit();

    expect(incrementSpy).toHaveBeenCalled();
  });
});
