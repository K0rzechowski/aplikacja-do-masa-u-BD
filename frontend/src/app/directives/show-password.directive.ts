import { Directive, ElementRef, HostListener, Input, Renderer2 } from '@angular/core';
import { MatIcon } from '@angular/material/icon';

@Directive({
  selector: '[appShowPassword]'
})
export class ShowPasswordDirective {

  private isShown = false;

  @Input('appShowPasswordIcon') iconEl!: MatIcon;
  @Input('appShowPasswordLabel') labelEl!: HTMLElement;
  @Input('appShowPasswordInput') inputEl!: HTMLInputElement;

  constructor(private el: ElementRef, private renderer: Renderer2) {}

  @HostListener("click")
  togglePassword() {
    if (!this.inputEl) {
      return;
    }
    
    this.isShown = !this.isShown;
    if (this.inputEl) {
      this.inputEl.type = this.isShown ? "text" : "password";
    }

    if (this.iconEl) {
      const el = this.iconEl._elementRef.nativeElement;
      this.renderer.setProperty(el, "innerText", this.isShown ? "visibility_off" : "visibility"); 
    }

    if (this.labelEl) {
      this.renderer.setProperty(this.labelEl, "innerText", this.isShown ? "Ukryj hasło" : "Pokaż hasło");
    }
  }

  @HostListener("mouseenter")
  onMouseEnter() {
    if (this.labelEl && !this.isShown) {
      this.renderer.setStyle(this.labelEl, 'visibility', 'visible');
    }
  }

  @HostListener("mouseleave")
  onMouseLeave() {
    if (this.labelEl && !this.isShown) {
      this.renderer.setStyle(this.labelEl, 'visibility', 'hidden');
    }
  }
}
