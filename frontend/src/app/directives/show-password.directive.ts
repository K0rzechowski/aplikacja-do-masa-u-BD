import { Directive, ElementRef, HostListener, Input, Renderer2 } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';

@Directive({
  selector: '[appShowPassword]'
})
export class ShowPasswordDirective {

  private isShown = false;

  @Input() appShowPasswordIcon!: MatIcon;
  @Input() appShowPasswordTooltip!: MatTooltip;
  @Input() appShowPasswordInput!: HTMLInputElement;

  constructor(private el: ElementRef, private renderer: Renderer2) {}

  @HostListener("click")
  togglePassword() {
    if (!this.appShowPasswordInput) {
      return;
    }
    
    this.isShown = !this.isShown;
    if (this.appShowPasswordInput) {
      this.appShowPasswordInput.type = this.isShown ? "text" : "password";
    }

    if (this.appShowPasswordIcon) {
      const el = this.appShowPasswordIcon._elementRef.nativeElement;
      this.renderer.setProperty(el, "innerText", this.isShown ? "visibility_off" : "visibility"); 
    }

    if (this.appShowPasswordTooltip) {
      this.appShowPasswordTooltip.message = this.isShown ? "Ukryj hasło" : "Pokaż hasło";
      this.appShowPasswordTooltip.show();
    }
  }
}
