import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../material.module';
import { TranslateModule } from '@ngx-translate/core';
//import { FieldFormatterPipe } from './pipes/field-formatter.pipe';

import { StepperComponent } from './components/stepper/stepper.component';
import { GButtonComponent } from './components/g-button/g-button.component';
import { SidenavBackdropComponent } from './components/sidenav-backdrop/sidenav-backdrop.component';
import { InternalFooterComponent } from './components/internal-footer/internal-footer.component';

@NgModule({
  declarations: [
    StepperComponent,
    GButtonComponent,
    SidenavBackdropComponent,
    InternalFooterComponent
    //FieldFormatterPipe,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    MaterialModule,
    TranslateModule.forChild(),
  ],
  exports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    MaterialModule,
    StepperComponent,
    GButtonComponent,
    TranslateModule,
    SidenavBackdropComponent,
    InternalFooterComponent
  ],
})
export class SharedModule {}
