import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './header/header.component';
import { SharedModule } from '../../shared/shared.module';
import { HeaderDesktopComponent } from './header/header-desktop/header-desktop.component';
import { HeaderMobileComponent } from './header/header-mobile/header-mobile.component';


@NgModule({
  declarations: [
    HeaderComponent,
    HeaderDesktopComponent,
    HeaderMobileComponent
  ],
  imports: [
    CommonModule,
    SharedModule
  ],
  exports: [HeaderComponent],
})
export class BaseModule { }
