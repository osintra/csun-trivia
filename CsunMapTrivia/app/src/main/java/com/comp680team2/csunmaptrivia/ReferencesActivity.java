/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * ReferencesActivity.java
 */

package com.comp680team2.csunmaptrivia;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ReferencesActivity extends Activity {
	private String[] list = {
			"http://articles.latimes.com/1999/jul/29/local/me-60647",
			"http://en.wikipedia.org/wiki/California_State_University,_Northridge",
			"http://hedev.com.s18707.gridserver.com/uploads/press/Arbor%20Court%20Opening.pdf",
			"http://library.csun.edu/About/HistoryandFacts",
			"http://library.csun.edu/About/LibraryColumns",
			"http://sundial.csun.edu/2011/03/csun-orange-grove-is-a-good-pick/",
			"http://www.csun.edu//buslab/index.html",
			"http://www.csun.edu/aboutCSUN/history/",
			"http://www.csun.edu/aboutCSUN/history/calstatenorthridge.html",
			"http://www.csun.edu/blaw/",
			"http://www.csun.edu/botanicgarden/about.html",
			"http://www.csun.edu/botanicgarden/tours.html",
			"http://www.csun.edu/career",
			"http://www.csun.edu/career/about-us",
			"http://www.csun.edu/catalog/academics/comp/programs/bs-computer-science/",
			"http://www.csun.edu/catalog/academics/fcs/programs/ba-family-and-consumer-sciences-i/apparel-design-and-merchandising/",
			"http://www.csun.edu/catalog/academics/phys/programs/bs-physics-ii/astrophysics/",
			"http://www.csun.edu/cobaessc/undergraduate-programs-business",
			"http://www.csun.edu/csundining/freudian-sip",
			"http://www.csun.edu/csundining/matador-bookstore-complex",
			"http://www.csun.edu/eisner-education/secondary-education/masters-programs",
			"http://www.csun.edu/eisner-education/secondary-education/mathematics-teaching-credential",
			"http://www.csun.edu/eisner-education/secondary-education/why-secondary-education",
			"http://www.csun.edu/engineering-computer-science/deans-message",
			"http://www.csun.edu/engineering-computer-science/electrical-computer-engineering/bs-electrical-engineering",
			"http://www.csun.edu/engineering-computer-science/mechanical-engineering",
			"http://www.csun.edu/facilities/ppm-services",
			"http://www.csun.edu/health-human-development/recreation-tourism-management/jason-wang",
			"http://www.csun.edu/humanities/chicana-chicano-studies/",
			"http://www.csun.edu/humanities/modern-classical-languages-literatures",
			"http://www.csun.edu/humanities/modern-classical-languages-literatures/japanese",
			"http://www.csun.edu/humanities/philosophy",
			"http://www.csun.edu/it/mediservequimontinst",
			"http://www.csun.edu/it/mediservequisageinst",
			"http://www.csun.edu/licensing/athletic-facilities",
			"http://www.csun.edu/licensing/conference-rooms-labs",
			"http://www.csun.edu/licensing/facilities-specialty-locations",
			"http://www.csun.edu/licensing/matador-square",
			"http://www.csun.edu/licensing/parking-structures",
			"http://www.csun.edu/licensing/restaurants",
			"http://www.csun.edu/licensing/university-buildings",
			"http://www.csun.edu/mike-curb-arts-media-communication/art",
			"http://www.csun.edu/mike-curb-arts-media-communication/art-galleries/gallery-store",
			"http://www.csun.edu/mike-curb-arts-media-communication/art/edward-alfano",
			"http://www.csun.edu/mike-curb-arts-media-communication/music/chamber-music",
			"http://www.csun.edu/mike-curb-arts-media-communication/music/csun-symphony",
			"http://www.csun.edu/mike-curb-arts-media-communication/music/ensembles-csun",
			"http://www.csun.edu/mike-curb-arts-media-communication/music/japanese-taiko-drumming-ensemble",
			"http://www.csun.edu/mike-curb-arts-media-communication/music/jazz-ensembles",
			"http://www.csun.edu/mike-curb-arts-media-communication/theatre",
			"http://www.csun.edu/mike-curb-arts-media-communication/theatre/accreditation",
			"http://www.csun.edu/node/19466",
			"http://www.csun.edu/node/19661",
			"http://www.csun.edu/node/23141",
			"http://www.csun.edu/orangegrovebistro/map",
			"http://www.csun.edu/orangegrovebistro/meetings-receptions",
			"http://www.csun.edu/parking",
			"http://www.csun.edu/parking/parking-enforcement",
			"http://www.csun.edu/parking/student-parking-information",
			"http://www.csun.edu/science-mathematics/chemistry-biochemistry",
			"http://www.csun.edu/science-mathematics/mathematics/recipients-abel-scholarship",
			"http://www.csun.edu/science-mathematics/news/chaparral-hall",
			"http://www.csun.edu/shc/",
			"http://www.csun.edu/shc/immunizations",
			"http://www.csun.edu/sites/default/files/common%20final%20rooms%20FA14.pdf",
			"http://www.csun.edu/sites/default/files/student-parking-information.pdf",
			"http://www.csun.edu/social-behavioral-sciences/geography",
			"http://www.csun.edu/social-behavioral-sciences/psychology/assessment-clinic",
			"http://www.csun.edu/social-behavioral-sciences/sociology/option-ii-criminology-and-criminal-justice",
			"http://www.csun.edu/src",
			"http://www.csun.edu/src/about",
			"http://www.csun.edu/src/about/building-features",
			"http://www.csun.edu/stufin",
			"http://www.csun.edu/stufin/ucs-how-pay-your-fees",
			"http://www.csun.edu/stufin/ucs-parking-fees",
			"http://www.csun.edu/tuc/matador-bookstore",
			"http://www.csun.edu/usu",
			"http://www.csun.edu/usu/about",
			"http://www.csun.edu/~hfout007/tours/tour_mag_cit_euca_oak.htm",
			"http://www.csun.edu/~hfout007/tours/tour_sierra_ctr.htm",
			"http://www.csun.edu/~sf70713/amccsun/",
			"http://www.emporis.com/complex/111641/california-state-university-northridge-los-angeles-ca-usa",
			"http://www.gomatadors.com/sports/w-tennis/coaches/index",
			"http://www.valleyperformingartscenter.org/press/factsheet/",
			"http://www.valleyperformingartscenter.org/press/fun-facts/",
			"https://sites.google.com/site/csunmsa/about-msa",
			"https://sites.google.com/site/csunmsa/membership"};

	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.references_activity);

		ListView listView = (ListView)findViewById(R.id.referencesListView1);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(list[position])));
			}
		});
		//"android.R.layout.simple_list_item_1" is a pre-defined layout that contains a TextView for
		//strings to be rendered with. Customized layouts can be used by replacing this argument.
		listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list));
    }
}