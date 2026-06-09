package com.team7.hboict.service;

import com.team7.hboict.dto.ElectionMapDto;
import com.team7.hboict.dto.ConstituencyMapDto;
import com.team7.hboict.model.Constituency;
import com.team7.hboict.model.ConstituencyResult;
import com.team7.hboict.model.Election;
import com.team7.hboict.repository.ConstituencyRepository;
import com.team7.hboict.repository.ElectionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DutchElectionServiceMapDataTest {

	@Mock
	private ElectionRepository electionRepository;

	@Mock
	private ConstituencyRepository constituencyRepository;

	@Mock
	private PartyColorService partyColorService;

	@InjectMocks
	private DutchElectionService dutchElectionService;

	private static ElectionMapDto byKey(List<ElectionMapDto> result, String hcKey) {
		return result.stream()
				.filter(dto -> hcKey.equals(dto.getHcKey()))
				.findFirst()
				.orElseThrow();
	}

	@Test
	void happyFlow_returns12Provinces_andAggregatesGroningenCorrectly() {
		// Arrange
		String electionId = "TK2023";
		when(partyColorService.getColorHex(anyString())).thenReturn("#000000");

		when(electionRepository.retrieve(electionId)).thenReturn(new Election(electionId));

		Constituency groningen = new Constituency("Groningen", "1");
		groningen.addPartyResult(new ConstituencyResult("PVV", 100));
		groningen.addPartyResult(new ConstituencyResult("VVD", 50));

		when(constituencyRepository.findByElectionId(electionId))
				.thenReturn(List.of(groningen));

		// Act
		List<ElectionMapDto> result = dutchElectionService.getRegionalElectionMapData(electionId);

		// Assert
		assertThat(result).hasSize(12);

		ElectionMapDto gr = byKey(result, "nl-gr");

		assertThat(gr.getWinningPartyName()).isEqualTo("PVV");
		assertThat(gr.getWinningPartyVotes()).isEqualTo(100);
		assertThat(gr.getTotalVotes()).isEqualTo(150);
		assertThat(gr.getWinningPartyColor()).isNotBlank();

		verify(constituencyRepository).findByElectionId(electionId);
		verify(partyColorService, times(12)).getColorHex(anyString());
	}

	@Test
	void happyFlow_multipleConstituenciesSameProvince_aggregatesAndPicksWinner() {
		// Arrange
		String electionId = "TK2023";
		when(partyColorService.getColorHex(anyString())).thenReturn("#000000");
		when(electionRepository.retrieve(electionId)).thenReturn(new Election(electionId));

		// 6 en 7 mappen beide naar Gelderland (nl-ge)
		Constituency nijmegen = new Constituency("Nijmegen", "6");
		nijmegen.addPartyResult(new ConstituencyResult("PVV", 10));
		nijmegen.addPartyResult(new ConstituencyResult("VVD", 5));

		Constituency arnhem = new Constituency("Arnhem", "7");
		arnhem.addPartyResult(new ConstituencyResult("PVV", 3));
		arnhem.addPartyResult(new ConstituencyResult("NSC", 20));

		when(constituencyRepository.findByElectionId(electionId))
				.thenReturn(List.of(nijmegen, arnhem));

		// Act
		List<ElectionMapDto> result = dutchElectionService.getRegionalElectionMapData(electionId);

		// Assert
		assertThat(result).hasSize(12);
		ElectionMapDto ge = byKey(result, "nl-ge");
		assertThat(ge.getWinningPartyName()).isEqualTo("NSC");
		assertThat(ge.getWinningPartyVotes()).isEqualTo(20);
		assertThat(ge.getTotalVotes()).isEqualTo(38);
		assertThat(ge.getWinningPartyColor()).isNotBlank();

		// Een provincie zonder stemmen blijft default 'Onbekend'
		ElectionMapDto dr = byKey(result, "nl-dr");
		assertThat(dr.getWinningPartyName()).isEqualTo("Onbekend");
		assertThat(dr.getWinningPartyVotes()).isZero();
		assertThat(dr.getTotalVotes()).isZero();

		verify(constituencyRepository).findByElectionId(electionId);
		verify(partyColorService, times(12)).getColorHex(anyString());
	}

	@Test
	void happyFlow_constituencyNumberWithLeadingZero_isNormalized() {
		// Arrange
		String electionId = "TK2023";
		when(partyColorService.getColorHex(anyString())).thenReturn("#000000");
		when(electionRepository.retrieve(electionId)).thenReturn(new Election(electionId));

		Constituency groningen = new Constituency("Groningen", "01");
		groningen.addPartyResult(new ConstituencyResult("PVV", 10));
		when(constituencyRepository.findByElectionId(electionId)).thenReturn(List.of(groningen));

		// Act
		List<ElectionMapDto> result = dutchElectionService.getRegionalElectionMapData(electionId);

		// Assert
		ElectionMapDto gr = byKey(result, "nl-gr");
		assertThat(gr.getWinningPartyName()).isEqualTo("PVV");
		assertThat(gr.getWinningPartyVotes()).isEqualTo(10);
		assertThat(gr.getTotalVotes()).isEqualTo(10);
		verify(partyColorService, times(12)).getColorHex(anyString());
	}

	@Test
	void unhappyFlow_invalidConstituencyNumber_isIgnoredAndNoVotesCounted() {
		// Arrange
		String electionId = "TK2023";
		when(partyColorService.getColorHex(anyString())).thenReturn("#000000");
		when(electionRepository.retrieve(electionId)).thenReturn(new Election(electionId));

		Constituency unknown = new Constituency("Onbekend", "99");
		unknown.addPartyResult(new ConstituencyResult("PVV", 999));
		when(constituencyRepository.findByElectionId(electionId)).thenReturn(List.of(unknown));

		// Act
		List<ElectionMapDto> result = dutchElectionService.getRegionalElectionMapData(electionId);

		// Assert
		assertThat(result).hasSize(12);
		ElectionMapDto gr = byKey(result, "nl-gr");
		assertThat(gr.getTotalVotes()).isZero();
		assertThat(gr.getWinningPartyName()).isEqualTo("Onbekend");
		assertThat(gr.getWinningPartyVotes()).isZero();
		verify(partyColorService, times(12)).getColorHex(anyString());
	}

	@Test
	void unhappyFlow_nullResultEntriesAndNullPartyName_areSkippedWithoutNpe() {
		// Arrange
		String electionId = "TK2023";
		when(partyColorService.getColorHex(anyString())).thenReturn("#000000");
		when(electionRepository.retrieve(electionId)).thenReturn(new Election(electionId));

		Constituency groningen = new Constituency("Groningen", "1");
		// forceer een null-entry in de lijst om de null-check in de service te testen
		groningen.getPartyResults().add(null);
		// partyName = null moet genegeerd worden
		groningen.addPartyResult(new ConstituencyResult(null, 123));
		// enige geldige entry
		groningen.addPartyResult(new ConstituencyResult("VVD", 7));
		when(constituencyRepository.findByElectionId(electionId)).thenReturn(List.of(groningen));

		// Act
		List<ElectionMapDto> result = dutchElectionService.getRegionalElectionMapData(electionId);

		// Assert
		ElectionMapDto gr = byKey(result, "nl-gr");
		assertThat(gr.getWinningPartyName()).isEqualTo("VVD");
		assertThat(gr.getWinningPartyVotes()).isEqualTo(7);
		assertThat(gr.getTotalVotes()).isEqualTo(7);
		verify(partyColorService, times(12)).getColorHex(anyString());
	}

	@Test
	void negativeFlow_electionMissingAndReadResultsFails_returnsEmptyList() {
		// Arrange
		String electionId = "TK2099";
		when(electionRepository.retrieve(electionId)).thenReturn(null);

		DutchElectionService spy = spy(new DutchElectionService(electionRepository, constituencyRepository, partyColorService));
		doReturn(null).when(spy).readResults(eq(electionId), anyString(), eq("constituencyOnly"));

		// Act
		List<ElectionMapDto> result = spy.getRegionalElectionMapData(electionId);

		// Assert
		assertThat(result).isEmpty();
		verify(constituencyRepository, never()).findByElectionId(anyString());
	}

	@Test
	void unhappyFlow_electionMissingButReadResultsSucceeds_stillReturns12Provinces() {
		// Arrange
		String electionId = "TK2099";
		when(partyColorService.getColorHex(anyString())).thenReturn("#000000");
		when(electionRepository.retrieve(electionId)).thenReturn(null);
		when(constituencyRepository.findByElectionId(electionId)).thenReturn(List.of());

		DutchElectionService spy = spy(new DutchElectionService(electionRepository, constituencyRepository, partyColorService));
		doReturn(new Election(electionId)).when(spy).readResults(eq(electionId), anyString(), eq("constituencyOnly"));

		// Act
		List<ElectionMapDto> result = spy.getRegionalElectionMapData(electionId);

		// Assert
		assertThat(result).hasSize(12);
		verify(constituencyRepository).findByElectionId(electionId);
		verify(partyColorService, times(12)).getColorHex(anyString());
	}

	@Test
	void happyFlow_constituencyMapSummary_returnsConstituenciesWithWinningPartyAndTotals() {
		// Arrange
		String electionId = "TK2023";
		when(partyColorService.getColorHex(anyString())).thenReturn("#000000");
		when(electionRepository.retrieve(electionId)).thenReturn(new Election(electionId));

		Constituency groningen = new Constituency("Groningen", "1");
		groningen.addPartyResult(new ConstituencyResult("PVV", 100));
		groningen.addPartyResult(new ConstituencyResult("VVD", 50));

		Constituency amsterdam = new Constituency("Amsterdam", "9");
		amsterdam.addPartyResult(new ConstituencyResult("GL-PvdA", 200));
		amsterdam.addPartyResult(new ConstituencyResult("VVD", 10));

		when(constituencyRepository.findByElectionId(electionId))
				.thenReturn(List.of(groningen, amsterdam));

		// Act
		List<ConstituencyMapDto> result = dutchElectionService.getConstituencyElectionMapData(electionId);

		// Assert
		assertThat(result).hasSize(2);

		ConstituencyMapDto gr = result.stream()
				.filter(dto -> "1".equals(dto.getConstituencyNumber()))
				.findFirst()
				.orElseThrow();
		assertThat(gr.getRegionName()).isEqualTo("Groningen");
		assertThat(gr.getWinningPartyName()).isEqualTo("PVV");
		assertThat(gr.getWinningPartyVotes()).isEqualTo(100);
		assertThat(gr.getTotalVotes()).isEqualTo(150);
		assertThat(gr.getWinningPartyColor()).isNotBlank();

		ConstituencyMapDto ams = result.stream()
				.filter(dto -> "9".equals(dto.getConstituencyNumber()))
				.findFirst()
				.orElseThrow();
		assertThat(ams.getWinningPartyName()).isEqualTo("GL-PvdA");
		assertThat(ams.getWinningPartyVotes()).isEqualTo(200);
		assertThat(ams.getTotalVotes()).isEqualTo(210);

		verify(constituencyRepository).findByElectionId(electionId);
		verify(partyColorService, atLeastOnce()).getColorHex(anyString());
	}


}
