import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DividerModule } from 'primeng/divider';
import { TagModule } from 'primeng/tag';

type FeatureItem = {
  icon: string;
  title: string;
  text: string;
};

type TestimonialItem = {
  name: string;
  role: string;
  text: string;
};

@Component({
  selector: 'app-landing-page',
  imports: [ButtonModule, CardModule, DividerModule, TagModule],
  templateUrl: './landing-page.html',
  styleUrl: './landing-page.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LandingPage {
  private readonly router = inject(Router);

  protected readonly features: FeatureItem[] = [
    {
      icon: 'pi pi-wallet',
      title: 'Conta Inteligente',
      text: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer faucibus purus non tristique aliquam.',
    },
    {
      icon: 'pi pi-bolt',
      title: 'Transferências Instantâneas',
      text: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris blandit orci sit amet leo malesuada posuere.',
    },
    {
      icon: 'pi pi-shield',
      title: 'Segurança em Camadas',
      text: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc sed lorem et eros convallis pulvinar.',
    },
  ];

  protected readonly testimonials: TestimonialItem[] = [
    {
      name: 'Cliente Exemplo',
      role: 'Freelancer',
      text: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur tincidunt sem ut pretium laoreet.',
    },
    {
      name: 'Usuário Teste',
      role: 'Empreendedora',
      text: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse condimentum neque vitae elit blandit auctor.',
    },
    {
      name: 'Pessoa Demo',
      role: 'Analista',
      text: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec viverra risus sed augue suscipit tempor.',
    },
  ];

  protected navigateToLogin(): void {
    void this.router.navigate(['/login']);
  }

  protected navigateToRegister(): void {
    void this.router.navigate(['/autocadastro']);
  }
}
